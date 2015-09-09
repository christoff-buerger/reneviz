package org.tud.reneviz.model.graphs;

import java.util.ArrayList;
import java.util.List;

public class Edge<N extends Node> {
	protected N _leftNode;
	protected N _rightNode;

	public Edge(N leftNode, N rightNode) {

		if (leftNode == null || rightNode == null) {
			throw new IllegalArgumentException();
		}

		this._leftNode = leftNode;
		this._rightNode = rightNode;
	}

	public List<N> getNodes() {
		List<N> nodes = new ArrayList<N>(2);
		nodes.add(_leftNode);
		nodes.add(_rightNode);
		return nodes;
	}

	public N getOtherNode(N node) {
		if (node == null) {
			throw new IllegalArgumentException();
		} else if (node.equals(_leftNode)) {
			return _rightNode;
		} else if (node.equals(_rightNode)) {
			return _leftNode;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_leftNode == null) ? 0 : _leftNode.hashCode());
		result = prime * result
				+ ((_rightNode == null) ? 0 : _rightNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Edge)) {
			return false;
		}

		@SuppressWarnings("rawtypes")
		Edge other = (Edge) obj;

		boolean nodesSwitched = false;

		if (_leftNode == null) {
			if (other._leftNode != null) {
				nodesSwitched = true;
			}
		} else if (!_leftNode.equals(other._leftNode)) {
			nodesSwitched = true;
		}
		if (_rightNode == null) {
			if (other._rightNode != null) {
				if (!nodesSwitched || other._leftNode != null) {
					return false;
				}
			}
		} else if (!_rightNode.equals(other._rightNode)) {
			if (!nodesSwitched || !_rightNode.equals(other._leftNode)) {
				return false;
			}
		}
		return true;
	}

}
