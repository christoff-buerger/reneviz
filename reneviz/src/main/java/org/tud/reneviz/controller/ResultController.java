package org.tud.reneviz.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.xerces.util.URI.MalformedURIException;
import org.primefaces.context.RequestContext;
import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.service.ResultService;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * The ResultController is the backing bean for the result part of the JSF
 * interface. All view-related matters in handling query results are located
 * here. Most methods are simply forwarding to the underlying
 * {@link ResultService}.
 * 
 */
@ManagedBean(name = "result")
@SessionScoped
public class ResultController {

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private ResultService resultService;

	private List<Map.Entry<String, String>> selectionStrings;

	private SimilarityEdge rowSelection_;
	
	private List<SimilarityEdge> queryResultList_; 

	public Resource getFocus() {
		return resultService.getFocus();
	}

	public List<SimilarityEdge> getQueryResultList() {
		
		if(queryResultList_ == null) {
			queryResultList_ = new ArrayList<SimilarityEdge>();
		}
		
		return queryResultList_;
	}

	public Resource getSelection() {
		return resultService.getSelection();
	}
	
	public String getSelectionLabel() {
		if(resultService.getSelection() != null && resultService.getSelectionResult() != null) {
			String label = resultService.getSelectionResult().getLabel();
			if(label==null || label.equals("")) {
				label = resultService.getSelection().getLocalName();
			}
			return label;
		}
		else return "";
		
	}
	
	public String getSelectionRDF() {
		try {
		if(resultService.getSelection() != null && resultService.getSelectionResult() != null) {
			String genericString = resultService.getSelection().getURI();
			org.apache.xerces.util.URI uri = new org.apache.xerces.util.URI(genericString);
			String id = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
			
			uri.setPath("/rdf/" + id + "/" + id + ".rdf");
			

			return uri.toString();
		}
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
	}

	public List<Map.Entry<String, String>> getSelectionStrings() {

		this.selectionStrings = resultService.getSelectionResult()
				.getSelectionStrings();

		return selectionStrings;
	}

	public void selectNode() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> map = facesContext.getExternalContext()
				.getRequestParameterMap();
		String nodeURI = map.get("nodeURI");

		if (this.getSelection() != null
				&& nodeURI.equals(this.getSelection().getURI())) {

			if(this.getFocus() != null
					&& nodeURI.equals(this.getFocus().getURI())) {
				logger.debug("removing focus");
				this.setFocus(null);
				
				RequestContext.getCurrentInstance().addCallbackParam("update", true);
			} else {
				logger.debug("A node has been focused in the Diagram: " + nodeURI);
				this.setFocus(ResourceFactory.createResource(nodeURI));

				RequestContext.getCurrentInstance().addCallbackParam("update", true);

				facesContext.addMessage(null,
						new FacesMessage("An element has been focused",
								resultService.getResult().getEdges().size()
								+ " result edges are shown."));
			}
			
			
		} else {
			logger.debug("A node has been selected in the Diagram: " + nodeURI);
			this.setSelection(ResourceFactory.createResource(nodeURI));

			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.addCallbackParam("update", false);
		}

	}

	public void setFocus(Resource focus) {
		if (focus == null) {
			resultService.setFocus(null);
		} else {
			if (!focus.equals(resultService.getFocus())) {
				resultService.setFocus(focus);
			}
		}
	}

	public void setSelection(Resource selection) {
		if (selection == null) {
			resultService.setSelection(null);
		} else {
			if (!selection.equals(resultService.getSelection())) {
				resultService.setSelection(resultService.getResult().getNode(selection));
				this.selectionStrings = resultService.getSelectionResult()
						.getSelectionStrings();
				logger.debug("Selection details updated with "
						+ this.selectionStrings.size() + " entries.");
			}
		}
	}

	public void updateResult(RequestContext context) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		this.selectionStrings = resultService.getSelectionResult()
				.getSelectionStrings();
		logger.debug("updateResult() called.");
		
		this.queryResultList_ = new ArrayList<SimilarityEdge>(resultService.getResult().getEdges());
		
		facesContext.addMessage(null,
				new FacesMessage("Search complete!",
						queryResultList_.size() + " of "
								+ resultService.getResult().getEdges().size()
								+ " result edges are shown."));
	}

	public SimilarityEdge getRowSelection() {
		return rowSelection_;
	}

	public void setRowSelection(SimilarityEdge rowSelection) {

		this.rowSelection_ = rowSelection;
		if (rowSelection != null) {
			logger.debug("A node has been selected: "
					+ rowSelection.getNodes().get(0));
			this.setSelection(rowSelection.getNodes().get(0).getResource());
		}

	}
	
	public String getLabel(Resource node) {
		return resultService.getResult().getNode(node).getLabel();
	}

}
