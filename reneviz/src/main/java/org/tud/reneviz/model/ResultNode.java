package org.tud.reneviz.model;

import java.util.Set;

import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.model.graphs.SimilarityNode;

public class ResultNode implements Comparable<ResultNode>{
	private SimilarityNode node_;
	private double similarity_;
	private Set<ResultNode> predecessors_;
	private Set<SimilarityEdge> predecessorEdges_;
	
	public ResultNode(SimilarityNode node, Double similarity, Set<ResultNode> predecessors, Set<SimilarityEdge> predecessorEdges) {
		this.node_ = node;
		this.similarity_ = similarity;
		this.predecessors_ = predecessors;
		this.predecessorEdges_ = predecessorEdges;
	}
	
	public SimilarityNode getNode() {
		return node_;
	}
	public void setNode(SimilarityNode node) {
		this.node_ = node;
	}
	public Double getSimilarity() {
		return similarity_;
	}
	public void setSimilarity(Double similarity) {
		this.similarity_ = similarity;
	}
	public Set<ResultNode> getPredecessors() {
		return predecessors_;
	}

	@Override
	public int compareTo(ResultNode o) {
		int comp = this.getSimilarity().compareTo(o.getSimilarity());
		if(comp == 0) {
			comp = this.getNode().getResource().getURI().compareTo(o.getNode().getResource().getURI());
		}
		return comp;
	}

	public Set<SimilarityEdge> getPredecessorEdges() {
		return predecessorEdges_;
	}


}
