package org.tud.reneviz.model.graphs;

public class SimilarityEdge extends Edge<SimilarityNode> {

	private int _score = 0;
	private double _simLeftToRight = 0.0d;
	private double _simRightToLeft = 0.0d;
	private double _sim = 0.0d;
	private SimilarityNode sourceNode_ = null;

	public SimilarityEdge(SimilarityNode left, SimilarityNode right) {
		super(left, right);
	}

	public SimilarityEdge(SimilarityNode left, SimilarityNode right, int score) {
		super(left, right);
		this._score = score;
	}

	public void setSimilarityFromNode(SimilarityNode node, double sim) {
		if (node != null) {
			if (node.equals(this._leftNode)) {
				this._simLeftToRight = sim;
			} else if (node.equals(this._rightNode)) {
				this._simRightToLeft = sim;
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
		this._sim = (this._simLeftToRight + this._simRightToLeft) / 2.0;
	}

	public double getSimilarityFromNode(SimilarityNode node) {
		if(node == null) {
			return this._sim;
		} else {
			if (node.equals(this._leftNode)) {
				return this._simLeftToRight;
			} else if (node.equals(this._rightNode)) {
				return this._simRightToLeft;
			}
		}
		throw new IllegalArgumentException();
	}

	public double getSimilarity() {
		return this._sim;
	}

	public int getScore() {
		return _score;
	}

	public void setScore(int _score) {
		this._score = _score;
	}

	public SimilarityNode getSourceNode() {
		return sourceNode_;
	}

	public void setSourceNode(SimilarityNode sourceNode) {
		this.sourceNode_ = sourceNode;
	}

}
