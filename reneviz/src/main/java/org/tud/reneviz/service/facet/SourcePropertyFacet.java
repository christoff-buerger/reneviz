package org.tud.reneviz.service.facet;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.vocabulary.RDF;

public class SourcePropertyFacet extends Facet {

	public SourcePropertyFacet(String name, Resource topic, Property property,
			double weight) {
		super(name, topic, property, weight);
		// TODO add direction?
	}

	public SourcePropertyFacet(String name, Resource topic,
			List<Property> propertyPath, double weight) {
		super(name, topic, propertyPath, weight);
		// TODO add direction?
	}

	protected Op getInnerQueryOp() {
		int rank = this.getPropertyPath().size() - 1;

		Node rdfType = RDF.type.asNode();

		Var memberVar = Var.alloc("member");

		List<Var> intermediateVarsSource = new ArrayList<Var>(rank);
		List<Expr> intermediateExprsSource = new ArrayList<Expr>(rank);

		for (int i = 0; i < rank; i++) {

			intermediateVarsSource.add(Var.alloc("intermediatevarsource" + i));
			intermediateExprsSource.add(new ExprVar(intermediateVarsSource
					.get(i)));
		}

		Var reasonVar = Var.alloc("reason");

		// compile basic patterns

		BasicPattern bp = new BasicPattern();

		
		// ?p1 rdf:type core:Project.
		bp.add(Triple.create(memberVar, rdfType, this.getTopic().asNode()));
		

		if (rank == 0) {
			// ?p1 core:domesticGeographicFocus ?o.
			bp.add(Triple.create(memberVar, this.getPropertyPath().get(0)
					.asNode(), reasonVar));
		} else {
			// ?source <prop0> ?ivs0
			bp.add(Triple.create(memberVar, this.getPropertyPath().get(0)
					.asNode(), intermediateVarsSource.get(0)));

			for (int i = 0; i < (rank - 1); i++) {
				// ?ivs(i) <prop(i+1)> ?ivs(i+1)
				bp.add(Triple.create(intermediateVarsSource.get(i), this
						.getPropertyPath().get(i + 1).asNode(),
						intermediateVarsSource.get(i + 1)));
			}

			// ?ivs(rank) <prop(rank+1)> ?reason.
			bp.add(Triple.create(intermediateVarsSource.get(rank - 1), this
					.getPropertyPath().get(rank).asNode(), reasonVar));
		}

		Op remoteOp = new OpBGP(bp);

		return remoteOp;
	}

}
