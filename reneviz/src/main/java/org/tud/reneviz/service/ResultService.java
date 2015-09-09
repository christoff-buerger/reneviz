package org.tud.reneviz.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.tud.reneviz.data.Constants;
import org.tud.reneviz.model.ResultNode;
import org.tud.reneviz.model.SelectionResult;
import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.model.graphs.SimilarityGraph;
import org.tud.reneviz.model.graphs.SimilarityNode;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_IsIRI;
import com.hp.hpl.jena.sparql.expr.E_IsLiteral;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;

@SessionScoped
public class ResultService implements Serializable {

	private static final long serialVersionUID = 6953253181760816519L;

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	SparqlService sparqlService;
	

	private SimilarityGraph result_;
	private SimilarityNode selection_;
	private SimilarityNode focus_;
	private SelectionResult selectionResult_;
	private SimilarityGraph focusResult_;


	public ResultService() {
		// do nothing. do everything in postConstruct instead
	}

	@PostConstruct
	public void postConstruct() {
		this.setSelectionResult(new SelectionResult());
		this.setResult(new SimilarityGraph());
	}

	public SimilarityGraph getResult() {
		
		// TODO third display type not considered
		
		if(getFocus() != null) {

			return focusResult_;
		} else {
			return result_;
		}
		
		
	}

	private SimilarityGraph getFullResult() {
		
		return result_;

	}
	
	public void setResult(SimilarityGraph result) {

		if (result == null) {
			result = new SimilarityGraph();
		}
		if (!result.equals(this.getResult())) {

			this.result_ = result;
		}

		
		if(!this.getResult().getNodes().contains(this.getSelection())) {
			this.setSelection(null);
		}
		
		if(this.getResult().getNode(this.getFocus()) == null) {
			logger.debug("new Result does not contain focus, unsetting");
			this.setFocus(null);
		} else {
			this.setFocus(this.getFocus());
		}
	}

	public Resource getSelection() {
		if(selection_ == null) {
			return null;
		} else {
			return selection_.getResource();
		}
		
	}

	public void setSelection(SimilarityNode selection) {
		this.selection_ = selection;

		if (selection == null) {
			// TODO fix this
			this.getSelectionResult().clear();
		} else {
			this.update();
		}
	}

	public Resource getFocus() {
		if(focus_ == null) {
			return null;
		} else {
			return focus_.getResource();
		}
		
	}

	public void setFocus(Resource focusResource) {

		SimilarityNode focus = this.getFullResult().getNode(focusResource);
		
		if (focus == null) {
			logger.debug("The focus has been set to null.");
			this.focus_ = focus;
			this.focusResult_ = new SimilarityGraph();
		} else if (true) {
			if (this.focus_ == null) {
				logger.debug("setFocus: resource " + focus.getResource().getLocalName()
						+ " is now focussed. (was null)");
			} else {
				logger.debug("setFocus: resource " + focus.getResource().getLocalName()
						+ " is now focussed. (was "
						+ this.focus_.getResource().getLocalName() + ")");
			}
			this.focus_ = focus;
			SimilarityGraph focusResult = new SimilarityGraph();

			Map<SimilarityNode, ResultNode> resultNodes = new HashMap<SimilarityNode, ResultNode>();
			TreeSet<ResultNode> fringe = new TreeSet<ResultNode>();

			fringe.add(new ResultNode(focus, 1.0D, new HashSet<ResultNode>(), new HashSet<SimilarityEdge>()));

			while (!fringe.isEmpty()
					&& resultNodes.size() < Constants.MAX_RESULT_LIST_SIZE) {
				
				// pop the element with the highest similarity (i.e. the last
				// element)
				ResultNode expandedNode = fringe.pollLast();

				// add it to the result nodes

				resultNodes.put(expandedNode.getNode(), expandedNode);
				
				// expand it (add neighborhood to fringe)
				for (SimilarityEdge newEdge : this.getFullResult()
						.getAdjacentEdges(expandedNode.getNode())) {

					// logger.info("expanding node");
					
					SimilarityNode newResult = newEdge.getOtherNode(expandedNode.getNode());
					
					double similarity = expandedNode.getSimilarity();
					similarity *= newEdge.getSimilarityFromNode(newResult);

					if (resultNodes.containsKey(newResult)) {
						// replace predecessor and similarity if necessary
					
						if (resultNodes.get(newResult).getSimilarity() < similarity) {
							resultNodes.get(newResult).getPredecessors().clear();
							resultNodes.get(newResult).getPredecessors().add(expandedNode);
							resultNodes.get(newResult).getPredecessorEdges().clear();
							resultNodes.get(newResult).getPredecessorEdges().add(newEdge);
							resultNodes.get(newResult).setSimilarity(similarity);
						} else if (resultNodes.get(newResult).getSimilarity() == similarity) {
							resultNodes.get(newResult).getPredecessors().add(expandedNode);
							resultNodes.get(newResult).getPredecessorEdges().add(newEdge);
						}
					} else {
						// logger.info("... that is added to fringe");
						Set<ResultNode> newPredecessors = new HashSet<ResultNode>();
						newPredecessors.add(expandedNode);
						
						Set<SimilarityEdge> newPredecessorEdges = new HashSet<SimilarityEdge>();
						newPredecessorEdges.add(newEdge);
						ResultNode newFringeNode = new ResultNode(newResult,
								similarity, newPredecessors, newPredecessorEdges);
						fringe.add(newFringeNode);
					}

				}

			}

			
			
			// fill focusResult nodes
			for (SimilarityNode resultNode : resultNodes.keySet()) {
				focusResult.addNode(this.getFullResult().getNode(resultNode.getResource()));

			}
			
			// fill focusResult edges
			for(SimilarityNode resultNode : focusResult.getNodes()) {
				Collection<SimilarityEdge> newEdges = this.getFullResult()
						.getAdjacentEdges(resultNode);
				for (SimilarityEdge newEdge : newEdges) {
					if (focusResult.getNodes().containsAll(newEdge.getNodes())) {
						newEdge.setSourceNode(null);
						focusResult.addEdge(newEdge);
					}
				}
			}


			// set priority nodes
			for (ResultNode resultNode : resultNodes.values()) {
				for(SimilarityEdge edge : resultNode.getPredecessorEdges()) {
					edge.setSourceNode(edge.getOtherNode(resultNode.getNode()));
				}
			}
			
			if(focusResult.getNodes().size() <= 1) {
				logger.debug("fokuzs result has only " + focusResult.getNodes().size() + " nodes and is therefore extended!");
				SimilarityGraph rest = result_.getSubgraph(Constants.MAX_RESULT_LIST_SIZE - 1);
				focusResult.addGraph(rest);
			}


			this.focusResult_ = focusResult;
			logger.debug("fokusResult has been set to " + this.focusResult_);
		}

	}


	public SelectionResult getSelectionResult() {
		return selectionResult_;
	}

	public void setSelectionResult(SelectionResult selectionResult) {
		this.selectionResult_ = selectionResult;
	}

	private Op createOp() {

		// constants

		Node rdfsLabel = NodeFactory
				.createURI("http://www.w3.org/2000/01/rdf-schema#label");

		// variables

		Var predicateVar = Var.alloc("predicate");
		Var objectVar = Var.alloc("object");
		Expr objectExpr = new ExprVar(objectVar);
		Var valueVar = Var.alloc("value");

		// compile basic pattern

		BasicPattern bp_spo = new BasicPattern();
		BasicPattern bp_label = new BasicPattern();

		// filters

		// isIRI
		Expr isIRIfilter = new E_IsIRI(objectExpr);

		// isLiteral
		Expr isLiteralFilter = new E_IsLiteral(objectExpr);

		// <resource> ?predicate ?object.
		bp_spo.add(Triple.create(this.getSelection().asNode(), predicateVar,
				objectVar));

		// ?object rdfs:label ?label
		bp_label.add(Triple.create(objectVar, rdfsLabel, valueVar));

		// compile query with operators

		// basic bgp
		Op opBGP_SPO = new OpBGP(bp_spo);

		// label
		Op opBGP_label = new OpBGP(bp_label);
		opBGP_label = OpFilter.filter(isIRIfilter, opBGP_label);

		// extend
		Op opExtend = OpTable.unit();
		opExtend = OpExtend.extend(opExtend, valueVar, objectExpr);
		opExtend = OpFilter.filter(isLiteralFilter, opExtend);

		// union
		Op union = OpUnion.create(opBGP_label, opExtend);

		// join
		Op op = OpJoin.create(opBGP_SPO, union);

		// SELECT
		List<Var> vars = new LinkedList<Var>();
		vars.add(predicateVar);
		vars.add(valueVar);
		op = new OpProject(op, vars);

		return op;
	}

	private void update() {
		Op op = createOp();

		SelectionResult selectionResult = sparqlService.runSelectionQuery(op);
		this.setSelectionResult(selectionResult);
	}

	public void reset() {
		// reset everything
		this.focus_ = null;
		this.focusResult_ = null;
		this.result_ = null;
		this.selection_ = null;
		this.selectionResult_ = null;
		
		// rebuild from scratch
		this.postConstruct();
		
	}

}
