package org.tud.reneviz.controller.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

/**
 * This converter is used to convert a double number between 0 and 1 to an
 * integer number between 0 and 100. Naturally, some rounding is involved.
 * The class is a CDI bean to make this functionality available in JSF.
 */
@Named
@ApplicationScoped
public class PercentConverter implements Converter {

	private static final double CONVERSION_FACTOR = 100D;
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		Integer percentValue = Integer.parseInt(value);
		Double doubleValue = percentValue.doubleValue() / CONVERSION_FACTOR;

		return doubleValue;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		Double doubleValue = (Double) value;

		Long longValue = Math.round(doubleValue * CONVERSION_FACTOR);

		return longValue.toString();
	}
}
