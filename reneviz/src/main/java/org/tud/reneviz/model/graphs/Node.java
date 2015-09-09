package org.tud.reneviz.model.graphs;

import com.hp.hpl.jena.rdf.model.Resource;

public abstract class Node {
	private Resource _resource;
	private String _label;

	public Node(Resource resource, String label) {
		if (resource == null) {
			throw new IllegalArgumentException();
		}

		this._resource = resource;
		this._label = (label == null) ? "" : label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_resource == null) ? 0 : _resource.hashCode());
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
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (_resource == null) {
			if (other._resource != null) {
				return false;
			}
		} else if (!_resource.equals(other._resource)) {
			return false;
		}
		return true;
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		this._label = (label == null) ? "" : label;
	}

	public Resource getResource() {
		return _resource;
	}

}
