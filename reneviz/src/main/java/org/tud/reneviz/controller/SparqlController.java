package org.tud.reneviz.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.tud.reneviz.service.JenaService;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;

/**
 * This is legacy code for a sparql interface. it is not necessary for reneviz.
 *
 */
@ManagedBean(name = "sparql")
public class SparqlController implements Serializable {

	private static final long serialVersionUID = 1923077721170069665L;

	@Inject
	JenaService jenaService;
	
	private Logger logger = Logger.getLogger(this.getClass());

	private String query;
	private ResultSetRewindable results;
	private String service;

	public SparqlController() {
		this.service = "http://localhost:3131/cornell/query";
		this.query = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix core: <http://vivoweb.org/ontology/core#>\n"
				+ "prefix acti: <http://vivoweb.org/ontology/activity-insight#>\n"
				+ "prefix reneviz: \n  <http://www.semanticweb.org/jm/ontologies/2013/9/reneviz#>\n"
				+ "SELECT *\n"
				+ "WHERE {\n"
				+ ""
				+ "?s ?p ?o.\n"
				+ "} LIMIT 100\n";
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<Map<String, RDFNode>> getResults() {

		if (results == null)
			return null;
		this.results.reset();

		List<Map<String, RDFNode>> results = new LinkedList<Map<String, RDFNode>>();

		for (; this.results.hasNext();) {
			QuerySolution soln = this.results.next();

			Map<String, RDFNode> newList = new TreeMap<String, RDFNode>();
			for (Iterator<String> var = soln.varNames(); var.hasNext();) {

				String varName = var.next();
				newList.put(varName, soln.get(varName));
			}
			results.add(newList);
		}
		return results;

	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<String> getResultColumns() {
		if (results == null) {
			return new LinkedList<String>();
		}
		return results.getResultVars();
	}

	public String getFormattedResults() {

		if (this.results == null)
			return "no query executed.";

		this.results.reset();
		
		String formattedResults = ResultSetFormatter.asText(this.results);

		formattedResults = StringEscapeUtils.escapeXml(formattedResults);

		formattedResults = "<pre>" + formattedResults + "</pre>";

		return formattedResults;
	}

	public String executeQuery() {

		Query query = QueryFactory.create(this.getQuery(), Syntax.syntaxARQ);

		Op op = Algebra.compile(query);

		try {
			
			if(new String("internal").equals(this.service)) {
				this.results = jenaService.runLocalOp(op);
			} else if (new String("external").equals(this.service)) {
				this.results = jenaService.runExternalOp(op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return ("success");
	}

}