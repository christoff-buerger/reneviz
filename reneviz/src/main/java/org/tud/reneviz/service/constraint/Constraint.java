package org.tud.reneviz.service.constraint;

import java.util.List;
import java.util.Observable;

import javax.faces.event.ActionEvent;

import org.tud.reneviz.model.FilterExpression;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.Var;

public abstract class Constraint extends Observable{
	public abstract Op applyConstraint(Op op, Var reason);

	public abstract List<FilterExpression> getExpressions();

	public abstract FilterExpression getExpression();

	public abstract void setExpression(FilterExpression expr);

	public abstract void setReady(Boolean ready);

	public abstract Boolean getReady();

	public abstract String getValue();

	public abstract void setValue(String value);

	public abstract void edit(ActionEvent actionEvent);

	public abstract void update(ActionEvent actionEvent);
}
