package org.tud.reneviz.model.graphs;

import com.hp.hpl.jena.rdf.model.Resource;

public class SimilarityNode extends Node {

	public SimilarityNode(Resource resource) {
		super(resource, null);
	}

	public SimilarityNode(Resource resource, String label) {
		super(resource, label);
	}

	private int _size = 0;
	private double _centrality = 0.0d;
	private double _strength = 0.0d;

	public double getCentrality() {
		return _centrality;
	}

	public void setCentrality(double centrality) {
		this._centrality = centrality;
	}

	public double getStrength() {
		return _strength;
	}

	public void setStrength(double strength) {
		this._strength = strength;
	}

	public int getSize() {
		return _size;
	}

	public void setSize(int size) {
		this._size = size;
	}

	@Override
	public String toString() {
		return "[SimilarityNode '"
				+ getLabel().substring(0, Math.min(30, getLabel().length())) + "']";
	}	
	
	
}
