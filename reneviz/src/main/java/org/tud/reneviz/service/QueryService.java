package org.tud.reneviz.service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.tud.reneviz.data.FacetRegistry;
import org.tud.reneviz.data.TopicRegistry;
import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.model.graphs.SimilarityGraph;
import org.tud.reneviz.model.graphs.SimilarityNode;
import org.tud.reneviz.service.facet.Facet;

import com.hp.hpl.jena.rdf.model.Resource;

@SessionScoped
public class QueryService implements Serializable {

	private static final long serialVersionUID = -9105168874711241372L;

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	ResultService resultService;

	@Inject
	FacetRegistry facetRegistry;

	@Inject
	TopicRegistry topicRegistry;

	@Inject
	SparqlService sparqlService;

	private Resource _topic;
	private List<Facet> _facets;

	public enum State {
		NEW, NOT_READY, READY
	}

	private State _state;

	public QueryService() {
		// do nothing. do everything in postConstruct instead
	}

	@PostConstruct
	public void postConstruct() {
		this.setState(State.NEW);

		this._topic = topicRegistry.getTopics().get(0);

		this._facets = new LinkedList<Facet>();

		// initialize facets
		for (Facet f : facetRegistry.getFacets(this.getTopic())) {
			this.addFacet(f);
		}
	}

	// public getters and setters

	public Resource getTopic() {
		return _topic;
	}

	public void setTopic(Resource topic) {

		if (!topic.equals(this.getTopic())) {
			this.setState(State.NEW);

			this._topic = topic;

			this.getFacets().clear();

			for (Facet f : facetRegistry.getFacets(this.getTopic())) {
				this.addFacet(f);
			}

			resultService.reset();
		}

	}

	public List<Facet> getFacets() {
		return _facets;
	}

	private void setState(State state) {
		this._state = state;
	}

	public State getState() {
		return this._state;
	}

	public void addFacet(Facet facet) {
		this._facets.add(facet);
	}

	public void removeFacet(Facet facet) {
		this._facets.remove(facet);
	}

	public void init() {

		if (this.getState() == State.NEW) {

			for (Facet f : getFacets()) {
				f.init(this.sparqlService);
			}

			this.setState(State.READY);

		}
	}

	public void executeQuery() {

		logger.debug("Starting the computation of the query results!");

		double weightSum = 0;
		for (Facet facet : this.getFacets()) {
			weightSum += facet.getWeight();
		}

		SimilarityGraph graph = new SimilarityGraph();

		// load labels
		graph.addNodes(sparqlService.getNodes(this.getTopic()));

		Facet smallestMandatoryFacet = null;
		int smallestMandatoryFacetSize = Integer.MAX_VALUE;

		for (Facet facet : this.getFacets()) {

			if (facet.getWeight() > 0.0D) {

				if (facet.getMandatory()
						&& facet.getResult().getEdges().size() < smallestMandatoryFacetSize) {
					smallestMandatoryFacet = facet;
					smallestMandatoryFacetSize = facet.getResult().getEdges()
							.size();
				}

				for (SimilarityEdge edge : facet.getResult().getEdges()) {

					for (SimilarityNode node : edge.getNodes()) {
						double similarity = ((double) (edge.getScore()))
								/ ((double) (node.getSize()));
						edge.setSimilarityFromNode(node, similarity);

						// TODO debug. remove later. or change to assert
						if (similarity > 1.0) {
							logger.error("similarity > 0: edge score=" + edge.getScore() + ", node size=" + node.getSize());
							throw new RuntimeException();
						}

					}

				}
			}
		}

		// if there is no mandatory facet: combine all!
		if (smallestMandatoryFacet == null) {
			for (Facet facet : this.getFacets()) {

				double facetWeight = facet.getWeight() / weightSum;

				for (SimilarityEdge edge : facet.getResult().getEdges()) {
					SimilarityEdge resultEdge = graph.getEdge(edge);
					for (SimilarityNode node : edge.getNodes()) {

						double addedSimilarity = edge
								.getSimilarityFromNode(node) * facetWeight;

						if (resultEdge != null) {
							resultEdge.setSimilarityFromNode(node,
									resultEdge.getSimilarityFromNode(node)
											+ addedSimilarity);
						} else {
							resultEdge = new SimilarityEdge(edge.getNodes().get(0), edge.getNodes().get(1));
							resultEdge.setSimilarityFromNode(node,
									edge.getSimilarityFromNode(node)
											* facetWeight);
						}
						graph.addEdge(resultEdge);
					}
				}
			}
		} else {

			// otherwise: combine the keys: take the shortest mandatory facet
			// and add the combined value of every entry (if it is contained)
			// in every mandatory facet
			for (SimilarityEdge edge : smallestMandatoryFacet.getResult().getEdges()) {
				
				boolean remove = false;
				
				SimilarityEdge resultEdge = new SimilarityEdge(edge.getNodes().get(0), edge.getNodes().get(1));
				
				
				for (SimilarityNode node : edge.getNodes()) {
					
					for (Facet facet : this.getFacets()) {
						
						double facetWeight = facet.getWeight() / weightSum;
						
						if (facet.getWeight() > 0.0D) {
							if (facet.getResult().getEdges().contains(edge)) {
								
								double addedSimilarity = edge
										.getSimilarityFromNode(node) * facetWeight;
								
								resultEdge.setSimilarityFromNode(node,
										resultEdge.getSimilarityFromNode(node)
												+ addedSimilarity);
								
								
							} else if (facet.getMandatory()) {
								remove = true;
							}
							if (!remove) {
								graph.addEdge(resultEdge);
							}
						}

					}
					
				}
				
				
			}
		}

		logger.debug("computation of query results finished with " + graph);

		graph.computeCentrality();
		
		logger.debug("Centrality computed!");
		
		// send the result to the ResultService
		resultService.setResult(graph);
	}

}
