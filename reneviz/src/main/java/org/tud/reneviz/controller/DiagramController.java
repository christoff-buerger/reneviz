package org.tud.reneviz.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.extensions.util.json.GsonConverter;
import org.tud.reneviz.service.DiagramService;


@ManagedBean(name = "diagram")
@SessionScoped
public class DiagramController {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Inject
	private DiagramService diagramService;

	public String getNodeLabels() {
		return diagramService.getNodeLabels().name();
	}
	public void setNodeLabels(String nodeLabels) {
		diagramService.setNodeLabels(DiagramService.LabelMode.valueOf(nodeLabels));
	}
	public String getEdgeLabels() {
		return diagramService.getEdgeLabels().name();
	}
	public void setEdgeLabels(String edgeLabels) {
		diagramService.setEdgeLabels(DiagramService.LabelMode.valueOf(edgeLabels));
	}
	public Boolean getEdges() {
		return diagramService.getEdges();
	}
	public void setEdges(Boolean edges) {
		diagramService.setEdges(edges);
	}
	public Boolean getAccuracy() {
		return diagramService.getAccuracy();
	}
	public void setAccuracy(Boolean accuracy) {
		diagramService.setAccuracy(accuracy);
	}
	public Boolean getRepel() {
		return diagramService.getRepel();
	}
	public void setRepel(Boolean repel) {
		diagramService.setRepel(repel);
	}
	
	public String getJson() {
		return GsonConverter.getGson().toJson(diagramService.getJson());
	}

	public List<String> getLabelModes() {
		List<String> results = new ArrayList<String>();
		for(DiagramService.LabelMode mode : DiagramService.LabelMode.values()) {
			results.add(mode.name());
		}
		return results;
	}
	
	public void update(ActionEvent actionEvent) {
	}
}
