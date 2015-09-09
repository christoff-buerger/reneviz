package org.tud.reneviz.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tud.reneviz.data.Constants;
import org.tud.reneviz.model.graphs.SimilarityEdge;
import org.tud.reneviz.model.graphs.SimilarityGraph;
import org.tud.reneviz.model.graphs.SimilarityNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Named
@SessionScoped
public class DiagramService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private ResultService resultService;

	public enum LabelMode {
		SHOW, HOVER, HIDE
	}

	private LabelMode nodeLabels;
	private LabelMode edgeLabels;

	private Boolean edges;
	private Boolean accuracy;
	private Boolean repel;

	@PostConstruct
	public void postconstruct() {

		this.setNodeLabels(LabelMode.HOVER);
		this.setEdgeLabels(LabelMode.HIDE);
		this.setAccuracy(false);
		this.setEdges(false);
		this.setRepel(false);

	}

	public LabelMode getNodeLabels() {
		return nodeLabels;
	}

	public void setNodeLabels(LabelMode nodeLabels) {
		this.nodeLabels = nodeLabels;
	}

	public LabelMode getEdgeLabels() {
		return edgeLabels;
	}

	public void setEdgeLabels(LabelMode edgeLabels) {
		this.edgeLabels = edgeLabels;
	}

	public Boolean getEdges() {
		return edges;
	}

	public void setEdges(Boolean edges) {
		this.edges = edges;
	}

	public Boolean getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Boolean accuracy) {
		this.accuracy = accuracy;
	}

	public Boolean getRepel() {
		return repel;
	}

	public void setRepel(Boolean repel) {
		this.repel = repel;
	}

	public JsonElement getJson() {

		Map<SimilarityNode, Integer> resourceMap = new HashMap<SimilarityNode, Integer>();

		JsonArray nodes = new JsonArray();
		

		int node_key = 0;

		SimilarityGraph results;
		if (resultService.getFocus() == null) {
			results = resultService.getResult().getSubgraph(Constants.MAX_RESULT_LIST_SIZE);

		} else {
			results = resultService.getResult();
		}

		
		for (SimilarityNode node : results.getNodes()) {

			JsonObject jsonNode = new JsonObject();
			jsonNode.add("index", new JsonPrimitive(node_key));
			jsonNode.add("label", new JsonPrimitive(node.getLabel()));
			jsonNode.add("uri", new JsonPrimitive(node.getResource().getURI()));
			

			jsonNode.add(
					"focus",
					new JsonPrimitive(node.getResource().equals(resultService
							.getFocus())));
			
			if(node.equals(resultService
					.getFocus())) {
				jsonNode.add("fixed", new JsonPrimitive(true));
			}

			jsonNode.add(
					"selection",
					new JsonPrimitive(node.getResource().equals(resultService
							.getSelection())));

			nodes.add(jsonNode);
			resourceMap.put(node, node_key);
			
			node_key++;

			
		}
		
		JsonArray links = new JsonArray();
		JsonArray bglinks = new JsonArray();
		JsonArray fglinks = new JsonArray();
		
		for (SimilarityEdge entry : results.getEdges()) {

			SimilarityNode source = entry.getNodes().get(0);
			SimilarityNode target = entry.getNodes().get(1);

			JsonObject link = new JsonObject();
			link.add("source", new JsonPrimitive(resourceMap.get(source)));
			link.add("target", new JsonPrimitive(resourceMap.get(target)));
			

			if (resultService.getFocus() != null) {
				
				SimilarityNode sourceNode = entry.getSourceNode();
				if(sourceNode != null) {
					link.add("priority", new JsonPrimitive(true));
					link.add("weight", new JsonPrimitive(entry.getSimilarityFromNode(sourceNode)));
					fglinks.add(link);
				} else  {
					link.add("priority", new JsonPrimitive(false));
					link.add("weight", new JsonPrimitive(entry.getSimilarityFromNode(sourceNode)));
					bglinks.add(link);
				}
				
				
			} else {
				link.add("priority", new JsonPrimitive(false));
				link.add("weight", new JsonPrimitive(entry.getSimilarity()));
				links.add(link);
			}

			

		}
		

		links.addAll(bglinks);
		links.addAll(fglinks);
		JsonObject result = new JsonObject();
		result.add("nodes", nodes);
		result.add("links", links);
		
		return result;

	}

}
