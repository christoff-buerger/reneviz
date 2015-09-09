package org.tud.reneviz.service.facet;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.vocabulary.RDF;

public class ChainPropertyFacet extends Facet {

	public ChainPropertyFacet(String name, Resource topic, Property property,
			double weight) {
		super(name, topic, property, weight);
	}

	public ChainPropertyFacet(String name, Resource topic,
			List<Property> propertyPath, double weight) {
		super(name, topic, propertyPath, weight);
	}

	protected Op getInnerQueryOp() {
		int rank = this.getPropertyPath().size() - 1;

		Node rdfType = RDF.type.asNode();

		Var memberVar = Var.alloc("member");
		Expr memberExpr = new ExprVar(memberVar);

		List<Var> intermediateVars = new ArrayList<Var>(rank);
		List<Expr> intermediateExprs = new ArrayList<Expr>(rank);

		for (int i = 0; i < rank; i++) {

			intermediateVars.add(Var.alloc("intermediatevar" + i));
			intermediateExprs.add(new ExprVar(intermediateVars.get(i)));
		}

		Var targetVar = Var.alloc("target");

		// compile basic patterns

		BasicPattern bp = new BasicPattern();

		
		// ?p1 rdf:type core:Project.
		bp.add(Triple.create(memberVar, rdfType, this.getTopic().asNode()));
		// ?p2 rdf:type core:Project.
		bp.add(Triple.create(targetVar, rdfType, this.getTopic().asNode()));
		

		// FILTER (iv(i) != source)
		List<Expr> intermediateFilters = new ArrayList<Expr>(rank);
		for (int i = 0; i < rank; i++) {
			intermediateFilters.add(new E_NotEquals(intermediateExprs.get(i),
					memberExpr));
		}

		if (rank == 0) {
			// ?source <prop(0)> ?target.
			bp.add(Triple.create(memberVar, this.getPropertyPath().get(0)
					.asNode(), targetVar));
		} else {
			// ?source <prop0> ?iv0
			bp.add(Triple.create(memberVar, this.getPropertyPath().get(0)
					.asNode(), intermediateVars.get(0)));

			for (int i = 0; i < (rank - 1); i++) {
				// ?ivs(i) <prop(i+1)> ?ivs(i+1)
				bp.add(Triple.create(intermediateVars.get(i), this
						.getPropertyPath().get(i + 1).asNode(),
						intermediateVars.get(i + 1)));
			}

			// ?iv(rank) <prop(rank+1)> ?target.
			bp.add(Triple.create(intermediateVars.get(rank - 1), this
					.getPropertyPath().get(rank).asNode(), targetVar));
		}

		Op op = new OpBGP(bp);

		for (Expr f : intermediateFilters) {
			op = OpFilter.filter(f, op);
		}

		return op;
	}

}
