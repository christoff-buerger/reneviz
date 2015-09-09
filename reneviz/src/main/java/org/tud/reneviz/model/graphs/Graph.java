package org.tud.reneviz.model.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Resource;

public class Graph<N extends Node, E extends Edge<N>> {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	protected Map<Resource, N> _nodes;
	private Map<E, E> _edges;

	private Map<N, Set<E>> _adjacentEdges;

	public Graph() {
		_nodes = new HashMap<Resource, N>();
		_edges = new HashMap<E, E>();
		_adjacentEdges = new HashMap<N, Set<E>>();
	}

	public void addNode(N node) {
		if (!_nodes.containsKey(node.getResource())) {
			_nodes.put(node.getResource(), node);
		}
	}
	
	public void addNodes(Map<Resource, N> nodes) {
		logger.info("adding nodes: before: " + _nodes.size());
		if (_nodes.isEmpty()) {
			_nodes = nodes;
		} else {
			_nodes.putAll(nodes);
		}
		logger.info("adding nodes: after: " + _nodes.size());
	}

	public void addEdge(E edge) {
		if (_edges.put(edge,edge) == null) {
			for (N node : edge.getNodes()) {
				if (!_adjacentEdges.containsKey(node)) {
					Set<E> nodeSet = new HashSet<E>();
					nodeSet.add(edge);
					_adjacentEdges.put(node, nodeSet);
				} else {
					_adjacentEdges.get(node).add(edge);
				}
			}
		}
	}

	public void addGraph(Graph<N,E> other) {
		for(N node : other.getNodes()) {
			this.addNode(node);
		}
		for(E edge : other.getEdges()) {
			this.addEdge(edge);
		}
	}
	
	public Collection<E> getAdjacentEdges(N node) {
		if(_adjacentEdges.containsKey(node)) {
			return _adjacentEdges.get(node);
		} else {
			// the node has no adjacent edges - that's nothing to worry about...
			return new HashSet<E>();
		}
		
	}

	public Collection<N> getNodes() {
		return _nodes.values();
	}
	
	public N getNode(Resource resource) {
		return _nodes.get(resource);
	}

	public E getEdge(E edge) {
		return _edges.get(edge);
	}
	
	public Set<E> getEdges() {
		return _edges.keySet();
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + ": " + _nodes.size() + " Nodes, " + _edges.size() + " Edges]";
	}
	
	
}
