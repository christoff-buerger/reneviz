package org.tud.reneviz.controller;

import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tud.reneviz.data.FacetRegistry;
import org.tud.reneviz.data.TopicRegistry;
import org.tud.reneviz.service.QueryService;
import org.tud.reneviz.service.facet.Facet;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The QueryController is the backing bean for the query part of the JSF
 * interface. All view-related matters in performing a query are handled here.
 * Most methods are simply forwarding to the underlying {@link QueryService}.
 *
 */
@Named("query")
@SessionScoped
public class QueryController {

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private FacetRegistry facetRegistry;

	@Inject
	private QueryService query;

	@Inject
	private ResultController result;

	@Inject
	private TopicRegistry topicRegistry;

	public void addFacet(Facet facet) {
		query.addFacet(facet);
	}
	
	public void addFacet(ActionEvent actionEvent) {
		// TODO add facet dialog
	}

	/**
	 * is called when the Query button is pressed; initiates both an update
	 * of the d3 diagram (via a callback parameter) and of the result table.
	 * @param actionEvent
	 */
	public void executeQuery(ActionEvent actionEvent) {

		logger.info("executing query");
		
		query.executeQuery();

		result.updateResult(RequestContext.getCurrentInstance());

	}

	public List<Facet> getFacets() {
		return query.getFacets();
	}

	public List<Facet> getPossibleFacets() {
		return facetRegistry.getFacets(query.getTopic());
	}

	public Resource getTopic() {
		return query.getTopic();
	}

	public List<Resource> getTopics() {
		return topicRegistry.getTopics();
	}


	/**
	 * initiates the query. necessary to run the queries for the facets in time.
	 */
	public void init() {
		query.init();
	}

	public boolean isReady() {
		return (query.getState() == QueryService.State.READY);
	}

	public void removeFacet(Facet facet) {
		query.removeFacet(facet);
	}


	public void setTopic(Resource topic) {
		if (topic != null && !topic.equals(query.getTopic())) {
			query.setTopic(topic);
		}

	}

}
