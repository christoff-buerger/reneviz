package org.tud.reneviz.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.servlet.api.Web;
import org.apache.log4j.Logger;
import org.tud.reneviz.data.Constants;
import org.tud.reneviz.data.FacetRegistry;
import org.tud.reneviz.data.TopicRegistry;
import org.tud.reneviz.model.Reneviz;
import org.tud.reneviz.service.facet.Facet;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@ApplicationScoped
public class JenaService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	@Web
	private ServletContext context;

	@Inject
	private FacetRegistry facetRegistry;

	@Inject
	TopicRegistry topicRegistry;

	private Dataset ds;

	private Model m;

	private Map<Integer, ResultSetRewindable> cache;

	public JenaService() {

	}

	@PostConstruct
	public void constructor() {

		// create the dataset for the copy of vivo
		String vivoDirectory = "/resources/jena/vivo";

		String realVivoDirectory = context.getRealPath(vivoDirectory);
		logger.info("The vivo dataset is stored in " + realVivoDirectory);

		this.ds = TDBFactory.createDataset(realVivoDirectory);
		this.m = ds.getDefaultModel();

		this.cache = new HashMap<Integer, ResultSetRewindable>();
	}

	public void cacheAll() {
		logger.info("Creating cache.");
		long startTime = System.currentTimeMillis();
		for (Resource topic : topicRegistry.getTopics()) {
			for (Facet facet : facetRegistry.getFacets(topic)) {
				this.cache(facet);
			}
		}
		long endTime = System.currentTimeMillis();
		String timeString = new SimpleDateFormat("mm:ss:SSS").format(new Date(
				endTime - startTime));

		logger.info("Cache created in " + timeString);
	}

	public void cache(Facet facet) {

		if (facet.isCached()) {
			logger.info("facet is already cached!");
			return;
		}

		// TODO!
		facet.setCached(true);

		Resource facetResource = null;

		try {

			ds.begin(ReadWrite.WRITE);

			// create facet identifier and add facet to local database
			facetResource = m.createResource();

			facetResource.addProperty(RDF.type, Reneviz.Facet);

			facetResource.addProperty(Reneviz.facetType, ResourceFactory
					.createPlainLiteral(facet.getClass().getName()));

			String properties = StringUtils.join(facet.getPropertyPath(), ";");
			facetResource.addProperty(Reneviz.propertySequence,
					ResourceFactory.createPlainLiteral(properties));

			facetResource.addProperty(Reneviz.hasTopic, facet.getTopic());
			ds.commit();

		} finally {
			ds.end();
		}

		// map reason -> cluster
		Map<RDFNode, Resource> clusters = new HashMap<RDFNode, Resource>();
		// map cluster -> cluster size
		Map<Resource, Integer> clusterSizes = new HashMap<Resource, Integer>();

		// run query and add facet results to the database
		ResultSetRewindable resultSet = runExternalOp(facet.getCreateCacheOp());

		try {

			ds.begin(ReadWrite.WRITE);
			resultSet.reset();
			for (; resultSet.hasNext();) {

				QuerySolution solution = resultSet.next();
				// variables in solution: reason, member

				RDFNode reason = solution.get("reason");
				RDFNode reasonLabel = solution.get("reasonlabel");

				Resource currentCluster;

				if (!clusters.containsKey(reason)) {
					Resource newCluster = m.createResource();
					clusters.put(reason, newCluster);
					facetResource.addProperty(Reneviz.hasCluster, newCluster);
					newCluster.addProperty(RDF.type, Reneviz.Cluster);
					newCluster.addProperty(Reneviz.hasReason, reason);

					if (reasonLabel != null) {
						m.add(reason.asResource(), RDFS.label, reasonLabel);
					}

					currentCluster = newCluster;

					clusterSizes.put(currentCluster, 1);
				} else {
					currentCluster = clusters.get(reason);
					clusterSizes.put(currentCluster,
							clusterSizes.get(currentCluster) + 1);
				}

				Resource member = solution.getResource("member");

				currentCluster.addProperty(Reneviz.hasMember, member);

			}

			for (Map.Entry<Resource, Integer> entry : clusterSizes.entrySet()) {
				entry.getKey().addProperty(Reneviz.size,
						entry.getValue().toString(), XSDDatatype.XSDinteger);
			}

			logger.info("Facet " + facet.getName() + "has " + clusters.size()
					+ " clusters");

			ds.commit();
			facet.setCached(true);

		} finally {
			ds.end();
		}

	}

	public ResultSetRewindable runLocalOp(Op op) {
		long startTime = System.currentTimeMillis();
		Query q = OpAsQuery.asQuery(op);
		logger.debug("Running query on the local dataset" + ":"
		// + "\n\nORIGINAL OP:\n"
		// + op.toString()
		// + "\n\nOPTIMIZED OP\n"
		// + Algebra.optimize(op)
				+ "\n\nSPARQL QUERY\n" + q.toString(Syntax.syntaxARQ));

		try {
			Integer key = op.toString().hashCode();
			if (cache.containsKey(key)) {
				logger.debug("The query was cached.");
				return cache.get(key);
			}

			ds.begin(ReadWrite.READ);

			QueryIterator qIter = Algebra.exec(op, this.ds);

			List<String> vars = new LinkedList<String>();
			for (Var var : OpAsQuery.asQuery(op).getProjectVars()) {
				vars.add(var.getVarName());
			}

			ResultSetRewindable results = ResultSetFactory
					.copyResults(ResultSetFactory.create(qIter, vars));

			long endTime = System.currentTimeMillis();
			String timeString = new SimpleDateFormat("mm:ss:SSS")
					.format(new Date(endTime - startTime));

			// cache disabled
			// cache.put(op.toString().hashCode(), results);

			logger.info("The query returned after " + timeString + " with "
					+ results.size() + " results");
			return results;
		} finally {
			ds.end();
		}
	}

	public ResultSetRewindable runExternalOp(Op op) {

		long startTime = System.currentTimeMillis();

		Query q = OpAsQuery.asQuery(op);
		logger.info("Running query on the external dataset:\n\n"
				+ "SPARQL QUERY\n" + q.toString(Syntax.syntaxARQ));

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Constants.getSparqlService(), q);

		ResultSetRewindable results = ResultSetFactory.copyResults(qexec
				.execSelect());

		long endTime = System.currentTimeMillis();
		String timeString = new SimpleDateFormat("mm:ss:SSS").format(new Date(
				endTime - startTime));

		logger.info("The query returned after " + timeString + " with "
				+ results.size() + " results");

		qexec.close();

		return results;
	}

	@PreDestroy
	public void destructor() {
		this.ds.close();
	}

	public void loadDatabase() {
		long startTime = System.currentTimeMillis();

		logger.info("loading data from " + Constants.getSparqlData());
		TDBLoader.loadModel(this.m, Constants.getSparqlData());

		long endTime = System.currentTimeMillis();
		String timeString = new SimpleDateFormat("mm:ss:SSS").format(new Date(
				endTime - startTime));

		logger.info("Local database loaded in " + timeString + " with "
				+ ds.getDefaultModel().size() + " statements");

	}

}
