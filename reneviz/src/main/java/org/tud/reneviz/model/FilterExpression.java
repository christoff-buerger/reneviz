package org.tud.reneviz.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LangMatches;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.E_StrContains;
import com.hp.hpl.jena.sparql.expr.E_StrEndsWith;
import com.hp.hpl.jena.sparql.expr.E_StrStartsWith;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;

public class FilterExpression {
	
	public enum Type {
		// special values:
		EMPTY,
		
		// general values
		EQUALS,
		NOT_EQUAL,
		
		// string values
		CONTAINS,
		NOT_CONTAINS,
		STARTSWITH,
		NOT_STARTSWITH,
		ENDSWITH,
		NOT_ENDSWITH,
		LANGMATCHES,
		NOT_LANGMATCHES,
		REGEX,
		NOT_REGEX,
		
		// number (and string) values
		LESS,
		LESSEQUAL,
		GREATER,
		GREATEREQAL
	}
	
	private static Map<Type, FilterExpression> expressions;
	
	private Type type;

	private String label;
	
	private static void init() {
		if(FilterExpression.expressions == null) {
			FilterExpression.expressions = new HashMap<FilterExpression.Type, FilterExpression>(Type.values().length);
			for(Type t : Type.values()) {
				FilterExpression.expressions.put(t, new FilterExpression(t));
			}
		}
	}
	
	public static FilterExpression create(Type type) {
		init();
		
		if(type == null) {
			return FilterExpression.expressions.get(Type.EMPTY);
		} else {
			return FilterExpression.expressions.get(type);
		}
	}
	
	public static FilterExpression create(String type) {
		init();
		
		for(FilterExpression e : FilterExpression.expressions.values()) {
			if(e.getLabel().equals(type)) {
				return e;
			}
		}
		
		return FilterExpression.expressions.get(Type.EMPTY);
	}
	
	public static List<FilterExpression> getExpressions() {
		init();
		return new LinkedList<FilterExpression>(FilterExpression.expressions.values());
	}
	
	private FilterExpression(Type type) {
		this.type = type;
		
		switch(type) {
		case CONTAINS:
			this.setLabel("contains");
			break;
		case EMPTY:
			this.setLabel("please chose...");
			break;
		case ENDSWITH:
			this.setLabel("ends with");
			break;
		case EQUALS:
			this.setLabel("=");
			break;
		case GREATER:
			this.setLabel(">");
			break;
		case GREATEREQAL:
			this.setLabel(">=");
			break;
		case LANGMATCHES:
			this.setLabel("has language");
			break;
		case LESS:
			this.setLabel("<");
			break;
		case LESSEQUAL:
			this.setLabel("<=");
			break;
		case NOT_CONTAINS:
			this.setLabel("does not contain");
			break;
		case NOT_ENDSWITH:
			this.setLabel("does not end with");
			break;
		case NOT_EQUAL:
			this.setLabel("!=");
			break;
		case NOT_LANGMATCHES:
			this.setLabel("does not have language");
			break;
		case NOT_REGEX:
			this.setLabel("does not match regex");
			break;
		case NOT_STARTSWITH:
			this.setLabel("does not start with");
			break;
		case REGEX:
			this.setLabel("matches regex");
			break;
		case STARTSWITH:
			this.setLabel("starts with");
			break;
		default:
			this.setLabel("<please chose>");
			break;
		}
	}
	
	public Expr getFilter(Expr expr, String valueString) {
		
		Expr value = new NodeValueString(valueString);
		
		switch(type) {
		case CONTAINS:
			return new E_StrContains(expr, value);
		case EMPTY:
			return null;
		case ENDSWITH:
			return new E_StrEndsWith(expr, value);
		case EQUALS:
			return new E_Equals(expr, value);
		case GREATER:
			return new E_GreaterThan(expr, value);
		case GREATEREQAL:
			return new E_GreaterThanOrEqual(expr, value);
		case LANGMATCHES:
			return new E_LangMatches(expr, value);
		case LESS:
			return new E_LessThan(expr, value);
		case LESSEQUAL:
			return new E_LessThanOrEqual(expr, value);
		case NOT_CONTAINS:
			return new E_LogicalNot(new E_StrContains(expr, value));
		case NOT_ENDSWITH:
			return new E_LogicalNot(new E_StrEndsWith(expr, value));
		case NOT_EQUAL:
			return new E_NotEquals(expr, value);
		case NOT_LANGMATCHES:
			return new E_LogicalNot(new E_LangMatches(expr, value));
		case NOT_REGEX:
			return new E_LogicalNot(new E_Regex(expr, value, NodeValue.makeString("")));
		case NOT_STARTSWITH:
			return new E_LogicalNot(new E_StrStartsWith(expr, value));
		case REGEX:
			return new E_Regex(expr, value, NodeValue.makeString(""));
		case STARTSWITH:
			return new E_StrStartsWith(expr, value);
		default:
			return null;
		}

	}

	public String getLabel() {
		return label;
	}

	private void setLabel(String label) {
		this.label = label;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return this.getLabel();
	}

}
