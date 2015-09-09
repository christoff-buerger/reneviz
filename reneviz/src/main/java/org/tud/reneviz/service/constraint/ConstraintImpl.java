package org.tud.reneviz.service.constraint;

import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.tud.reneviz.model.FilterExpression;
import org.tud.reneviz.model.FilterExpression.Type;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Conditional;
import com.hp.hpl.jena.sparql.expr.E_IsIRI;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ConstraintImpl extends Constraint {

	protected Logger logger = Logger.getLogger(this.getClass());

	private Boolean ready;
	private FilterExpression expression_;

	private String value;

	public ConstraintImpl() {
		this.setValue("");
		this.ready = false;
		this.setExpression(FilterExpression.create(Type.EMPTY));
	}

	@Override
	public Op applyConstraint(Op op, Var reason) {
		
		logger.debug("Applying constraint with value='" + this.getValue() + "' and expression=" + this.getExpression());
		
		if (value == null || value.isEmpty()) {
			return op;
		}


		Node rdfsLabel = RDFS.label.asNode();

		Var reasonLabel = Var.alloc("reasonlabel");
		Expr reasonLabelExpr = new ExprVar(reasonLabel);

		Expr reasonExpr = new ExprVar(reason);
		
		// create Triple
		BasicPattern optionalPattern = new BasicPattern();

		// ?source rdfs:label ?sourcelabel.
		optionalPattern
				.add(Triple.create(reason, rdfsLabel, reasonLabel));

		
		op = OpLeftJoin.create(op, new OpBGP(optionalPattern),new ExprList());

		Expr filter = new E_Conditional(
				new E_IsIRI(reasonExpr),
				reasonLabelExpr,
				reasonExpr);
		
		filter = this.getExpression().getFilter(filter, value);
		if (filter == null) {
			return op;
		}
		
		return OpFilter.filter(filter, op);

	}

	@Override
	public String toString() {
		return "[Constraint: Value='" + value + "', Expression='"
				+ this.getExpression().getLabel() + "']";
	}

	@Override
	public List<FilterExpression> getExpressions() {
		return FilterExpression.getExpressions();
	}

	@Override
	public FilterExpression getExpression() {
		return this.expression_;
	}

	@Override
	public void setExpression(FilterExpression expr) {
		if (expr == null) {
			this.expression_ = FilterExpression.create(Type.EMPTY);
		} else {
			this.expression_ = expr;
		}
	}

	@Override
	public Boolean getReady() {
		return this.ready;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void edit(ActionEvent actionEvent) {
		this.setReady(false);
	}

	@Override
	public void update(ActionEvent actionEvent) {
		this.setReady(true);
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public void setReady(Boolean ready) {
		this.ready = ready;

	}

}
