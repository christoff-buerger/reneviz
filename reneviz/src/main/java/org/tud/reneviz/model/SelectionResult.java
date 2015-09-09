package org.tud.reneviz.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class stores the details of a selected resource.
 * 
 */
public class SelectionResult {

	protected Logger logger = Logger.getLogger(this.getClass());	
	
	private Map<RDFNode, List<Literal>> map_;
	private Map<String, String> stringMap_;

	public SelectionResult() {
		map_ = new HashMap<RDFNode, List<Literal>>();
		stringMap_ = new HashMap<String, String>();
	}

	protected Map<RDFNode, List<Literal>> getMap() {
		return map_;
	}

	protected Map<String, String> getStringMap() {
		return stringMap_;
	}
	
	public void clear() {
		map_.clear();
		stringMap_.clear();
	}

	public void addResult(RDFNode property, Literal value) {
		String valueString = "";

		if (getMap().containsKey(property)) {
			valueString = getStringMap().get(property.asNode().getLocalName()) + ", ";
		} else {
			getMap().put(property, new LinkedList<Literal>());

		}

		valueString += value.getString();
		getStringMap().put(property.asNode().getLocalName(), valueString);

		getMap().get(property).add(value);
	}

	/**
	 * @return a {@link List} with the {@link Property} names as key
	 * and a String that is the concatenation of all values of this property as
	 * value.
	 */
	public List<Map.Entry<String, String>> getSelectionStrings() {

		return new LinkedList<Map.Entry<String, String>>(getStringMap().entrySet());
	}
	
	public int size() {
		return map_.size();
	}
	
	public String getLabel() {
		
		if (getMap().containsKey(RDFS.label.asResource())) {
			return getStringMap().get(RDFS.label.getLocalName());
		} else {
			return null;

		}
	}

}
