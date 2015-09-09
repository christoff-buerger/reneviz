package org.tud.reneviz.data;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * This is a CDI bean that offers a registry for search topics.
 * 
 */
@ApplicationScoped
public class TopicRegistry {

	private List<Resource> topics;
	
	public TopicRegistry() {
		this.topics = new LinkedList<Resource>();
		this.addTopics();
	}
	
	private void addTopics() {
		String[] topicNames = {
				"http://vivoweb.org/ontology/core#FacultyMember",
				"http://xmlns.com/foaf/0.1/Person",
				"http://xmlns.com/foaf/0.1/Organization",
				"http://vivoweb.org/ontology/core#Project",
				"http://purl.org/ontology/bibo/Conference",
				"http://purl.org/ontology/bibo/Workshop",
				"http://vivoweb.org/ontology/core#Grant",
				"http://purl.org/NET/c4dm/event.owl#Event"
				};


		for (String t : topicNames) {
			this.topics.add(ResourceFactory.createResource(t));
		}
		
	}	
	
	public List<Resource> getTopics() {
		return this.topics;
	}
}
