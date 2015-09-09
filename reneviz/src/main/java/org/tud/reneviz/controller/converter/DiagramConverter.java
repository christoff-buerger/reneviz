package org.tud.reneviz.controller.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

import org.apache.log4j.Logger;

@Named
@ApplicationScoped
public class DiagramConverter implements Converter {

	private Logger logger = Logger.getLogger(this.getClass());
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return value;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}
}
