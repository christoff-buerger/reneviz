package org.tud.reneviz.controller.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tud.reneviz.model.FilterExpression;

/**
 * This converter converts the URI of a resource to a JENA
 * {@link com.hp.hpl.jena.rdf.model.Resource} and vice versa. The class is a CDI
 * bean to make this functionality available in JSF.
 */
@Named
@ApplicationScoped
public class FilterExpressionConverter implements Converter {

	private Logger logger = Logger.getLogger(this.getClass());
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		FilterExpression expr = FilterExpression.create(value);
		return expr;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}
}
