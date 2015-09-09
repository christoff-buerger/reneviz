package org.tud.reneviz.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.tud.reneviz.model.SelectionResult;
import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.model.graphs.SimilarityGraph;
import org.tud.reneviz.model.graphs.SimilarityNode;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@ApplicationScoped
public class SparqlService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private JenaService jenaService;

	public SparqlService() {

	}

	public SimilarityGraph runFacetQuery(Op edgeOp, Op sizeOp) {

		SimilarityGraph graph = new SimilarityGraph();

		ResultSetRewindable sizeResults = jenaService.runLocalOp(sizeOp);
		
		sizeResults.reset();
				
		for (; sizeResults.hasNext();) {
			QuerySolution soln = sizeResults.next();

			if (soln.get("node")!= null && soln.get("score") != null) {
				Resource resource = soln.get("node").asResource();
				SimilarityNode node = new SimilarityNode(resource);
				
				int size = soln.get("score").asLiteral().getInt();
				node.setSize(size);
				if(size < 1) {
					logger.error("node with size " + size);
				}
				graph.addNode(node);
			} else {
				// logger.info("error adding score");
			}

		}
			
		ResultSetRewindable edgeResults = jenaService.runLocalOp(edgeOp);

		edgeResults.reset();
		
		for (; edgeResults.hasNext();) {
			QuerySolution soln = edgeResults.next();

			if (soln.get("source")!= null && soln.get("target") != null) {
				Resource source = soln.get("source").asResource();
				Resource target = soln.get("target").asResource();

				int score = soln.getLiteral("score").getInt();
				if (source != null && target != null && score > 0) {
					SimilarityNode sourceNode = graph.getNode(source);
					SimilarityNode targetNode = graph.getNode(target);
					SimilarityEdge edge = new SimilarityEdge(sourceNode, targetNode, score);
					
					graph.addEdge(edge);
				}
			}
		}

		return graph;
	}

	
	public Map<Resource,SimilarityNode> getNodes(Resource topic) {
		
		Var nodeVar = Var.alloc("node");
		Var labelVar = Var.alloc("label");
		
		// pattern
		BasicPattern requiredPattern = new BasicPattern();

		requiredPattern.add(Triple.create(
				nodeVar,
				RDF.type.asNode(),
				topic.asNode()));
		
		BasicPattern optionalPattern = new BasicPattern();
		
		optionalPattern.add(Triple.create(
				nodeVar,
				RDFS.label.asNode(),
				labelVar));

		Op requiredOp = new OpBGP(requiredPattern);
		Op optionalOp = new OpBGP(optionalPattern);
		
		
		// SELECT
		List<Var> vars = new LinkedList<Var>();
		vars.add(nodeVar);
		vars.add(labelVar);
		

		
		Op op = OpLeftJoin.create(requiredOp, optionalOp, new ExprList());
		
		op = new OpProject(op, vars);
		
		ResultSetRewindable labelResults = jenaService.runExternalOp(op);
		
		labelResults.reset();
		
		Map<Resource, SimilarityNode> nodes = new HashMap<Resource, SimilarityNode>();
		
		for (; labelResults.hasNext();) {
			QuerySolution soln = labelResults.next();

			if (soln.get("node")!= null) {
				Resource resource = soln.get("node").asResource();
				
				RDFNode labelNode = soln.get("label");
				String label = "";
				if(labelNode != null) {
					label = soln.get("label").asLiteral().getString();
				}
				nodes.put(resource, new SimilarityNode(resource, label));
			} else {
				logger.error("node could not be added");
			}
		}
		return nodes;
		
	}
	
	
	public SelectionResult runSelectionQuery(Op op) {

		SelectionResult result = new SelectionResult();

		ResultSetRewindable results = jenaService.runExternalOp(op);
		
		results.reset();
		
		for (; results.hasNext();) {
			QuerySolution soln = results.next();

			RDFNode predicate = soln.get("predicate");
			RDFNode object = soln.get("value");

			Literal literal = object.asLiteral();
			if (!literal.getString().isEmpty()) {
				result.addResult(predicate, literal);
			}
		}

		return result;
	}

}
