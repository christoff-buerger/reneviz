package org.tud.reneviz.service.facet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SlideEndEvent;
import org.tud.reneviz.model.Reneviz;
import org.tud.reneviz.model.graphs.SimilarityGraph;
import org.tud.reneviz.service.SparqlService;
import org.tud.reneviz.service.constraint.Constraint;
import org.tud.reneviz.service.constraint.ConstraintImpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCount;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public abstract class Facet implements Observer{
	
	protected Logger logger = Logger.getLogger(this.getClass());


	private SparqlService sparqlService;
	
	public void setSparqlService(SparqlService sparqlService) {
		this.sparqlService = sparqlService;
	}


	private String name_;
	

	private Resource topic_;
	private List<Property> propertyPath;
	private List<Constraint> constraints;
	private SimilarityGraph facetSimilarityGraph_;
	
	private boolean mandatory_;
	private double weight_;
	private boolean cached_;
	
	private boolean ready_;
	

	public Facet(String name, Resource topic, Property property, Double weight) {
		this.topic_ = topic;
		this.name_ = name;
		List<Property> propertyPath = Collections.singletonList(property);
		this.propertyPath = propertyPath;
		this.constraints = new LinkedList<Constraint>();
		
		// TODO find out actual cache state
		this.setCached(false);
		
		this.weight_ = weight;
	}

	public Facet(String name, Resource topic, List<Property> propertyPath, Double weight) {
		this.topic_ = topic;
		this.name_ = name;
		this.propertyPath = propertyPath;
		this.constraints = new LinkedList<Constraint>();
		
		// TODO find out actual cache state
		this.setCached(false);
		
		this.weight_ = weight;
	}

	protected Op getQueryOp() {

		Var sourceVar = Var.alloc("source");
		Expr sourceExpr = new ExprVar(sourceVar);
		
		Var targetVar = Var.alloc("target");
		Expr targetExpr = new ExprVar(targetVar);
		

		// FILTER (STR(?p1) < STR(?p2))
		Expr filter = new E_LessThan(new E_Str(sourceExpr), new E_Str(
				targetExpr));
		
		Op op = OpFilter.filter(filter, new OpBGP(this.getCacheQueryPattern()));

		return op;
	}
	
	protected BasicPattern getCacheQueryPattern() {

		Var facetVar = Var.alloc("facet");
		
		Var clusterVar = Var.alloc("cluster");
		Var reasonVar = Var.alloc("reason");
		
		Var sourceVar = Var.alloc("source");
		Var targetVar = Var.alloc("target");
		
		// local pattern
		BasicPattern bp = new BasicPattern();

		// ?facet rdf:type reneviz:Facet.
		bp.add(Triple.create(
				facetVar,
				RDF.type.asNode(),
				Reneviz.Facet.asNode()));
		
		// ?facet reneviz:facetType <this.getClass().getName()>.
		bp.add(Triple.create(
				facetVar,
				Reneviz.facetType.asNode(),
				NodeFactory.createLiteral(this.getClass().getName())));

		// ?facet reneviz:hasTopic <this.getTopic()>.
		bp.add(Triple.create(
				facetVar,
				Reneviz.hasTopic.asNode(),
				this.getTopic().asNode()));
		
		// ?facet reneviz:propertySequence <this.getPropertyPath()>.
		Node propertySequence = NodeFactory.createLiteral(StringUtils.join(this.getPropertyPath(), ";"));
		bp.add(Triple.create(
				facetVar,
				Reneviz.propertySequence.asNode(),
				propertySequence));

		// ?facet reneviz:hasCluster ?cluster.
		bp.add(Triple.create(
				facetVar,
				Reneviz.hasCluster.asNode(),
				clusterVar));
				
		// ?cluster reneviz:hasReason ?reason.
		bp.add(Triple.create(
				clusterVar,
				Reneviz.hasReason.asNode(),
				reasonVar));
		
		// ?cluster reneviz:hasMember ?source.
		bp.add(Triple.create(
				clusterVar,
				Reneviz.hasMember.asNode(),
				sourceVar));
		
		// ?cluster reneviz:hasMember ?target.
		bp.add(Triple.create(
				clusterVar,
				Reneviz.hasMember.asNode(),
				targetVar));

		return bp;
	}
	
	protected abstract Op getInnerQueryOp();
	
	public Op getCreateCacheOp() {

		Var memberVar = Var.alloc("member");
		Var reasonVar = Var.alloc("reason");
		Var reasonLabelVar = Var.alloc("reasonlabel");
		
		Op op = this.getInnerQueryOp();

		BasicPattern optionalPattern = new BasicPattern();

		optionalPattern
				.add(Triple.create(reasonVar, RDFS.label.asNode(), reasonLabelVar));

		
		op = OpLeftJoin.create(op, new OpBGP(optionalPattern),new ExprList());		
		
		// SELECT DISTINCT
		List<Var> vars = new LinkedList<Var>();
		vars.add(memberVar);
		vars.add(reasonVar);
		vars.add(reasonLabelVar);
		op = new OpProject(op, vars);
		op = new OpDistinct(op);

		return op;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		this.name_ = name;
	}

	public List<Property> getPropertyPath() {
		return propertyPath;
	}

	protected void setPropertyPath(List<Property> propertyPath) {
		this.propertyPath = propertyPath;
	}
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public void addConstraint() {
		Constraint c = new ConstraintImpl();
		c.addObserver(this);
		this.getConstraints().add(c);
	}
	
	public void removeConstraint(Constraint constraint) {
		this.getConstraints().remove(constraint);
		this.runQuery();
	}

	public Boolean getMandatory() {
		return mandatory_;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory_ = mandatory;
	}

	public boolean isReady() {
		return ready_;
	}

	protected void setReady(boolean ready) {
		this.ready_ = ready;
	}
	
	public Resource getTopic() {
		return topic_;
	}

	public void setTopic(Resource topic) {
		this.topic_ = topic;
	}

	public Double getWeight() {
		return weight_;
	}

	public void setWeight(Double weight) {
		this.weight_ = weight;
	}

	public SimilarityGraph getResult() {
		
		return this.facetSimilarityGraph_;
		
	}
	
	public Op getCreateExtraCacheOp() {
		// TODO 
		
		return null;
		
	}

	protected Op getFullOp() {
		
		Var sourceVar = Var.alloc("source");
		Var reasonVar = Var.alloc("reason");
		Var targetVar = Var.alloc("target");
		Var score = Var.alloc("score");
		Var v1 = Var.alloc("v1");

		Expr scoreExpr = new ExprVar(score);
		
		Op op = this.getQueryOp();

		// CONSTRAINTS
		
		for (Constraint constraint: this.getConstraints()) {
			op = constraint.applyConstraint(op, reasonVar);
		}
		
		// GROUPBY
		VarExprList groupVars = new VarExprList();
		groupVars.add(sourceVar);
		groupVars.add(targetVar);
		List<ExprAggregator> aggregators = new LinkedList<ExprAggregator>();

		Aggregator countAggregator = new AggCount();
		aggregators.add(new ExprAggregator(score, countAggregator));
		op = new OpGroup(op, groupVars, aggregators);

		// EXTEND (not sure why this is necessary) TODO
		VarExprList exprs = new VarExprList();
		exprs.add(v1, scoreExpr);
		op = OpExtend.extend(op, exprs);

		// SELECT
		List<Var> vars = new LinkedList<Var>();
		vars.add(sourceVar);
		vars.add(targetVar);
		vars.add(score);
		op = new OpProject(op, vars);

		return op;
	}
	
protected Op getScoreOp() {
		
//			SELECT ?node (COUNT (*) AS ?score)
//			WHERE {
//			  ?facet rdf:type reneviz:Facet .
//			  ?facet reneviz:facetType "org.tud.reneviz.service.facet.SourcePropertyFacet" .
//			  ?facet reneviz:hasTopic core:Project .
//			  ?facet reneviz:propertySequence "http://vivoweb.org/ontology/core#domesticGeographicFocus" .
//			  ?facet reneviz:hasCluster ?cluster .
//			  ?cluster reneviz:hasMember ?node.
//			} 
//			GROUP BY ?node
//			ORDER BY DESC(?score)
	
		Var nodeVar = Var.alloc("node");
		Var facetVar = Var.alloc("facet");
		Var clusterVar = Var.alloc("cluster");
		Var scoreVar = Var.alloc("score");
		Var v1 = Var.alloc("v1");

		Expr scoreExpr = new ExprVar(scoreVar);
		
		// pattern
		BasicPattern bp = new BasicPattern();

		// ?facet rdf:type reneviz:Facet.
		bp.add(Triple.create(
				facetVar,
				RDF.type.asNode(),
				Reneviz.Facet.asNode()));
		
		// ?facet reneviz:facetType <this.getClass().getName()>.
		bp.add(Triple.create(
				facetVar,
				Reneviz.facetType.asNode(),
				NodeFactory.createLiteral(this.getClass().getName())));

		// ?facet reneviz:hasTopic <this.getTopic()>.
		bp.add(Triple.create(
				facetVar,
				Reneviz.hasTopic.asNode(),
				this.getTopic().asNode()));
		
		// ?facet reneviz:propertySequence <this.getPropertyPath()>.
		Node propertySequence = NodeFactory.createLiteral(StringUtils.join(this.getPropertyPath(), ";"));
		bp.add(Triple.create(
				facetVar,
				Reneviz.propertySequence.asNode(),
				propertySequence));

		// ?facet reneviz:hasCluster ?cluster.
		bp.add(Triple.create(
				facetVar,
				Reneviz.hasCluster.asNode(),
				clusterVar));
		
		// ?cluster reneviz:hasMember ?node.
		bp.add(Triple.create(
				clusterVar,
				Reneviz.hasMember.asNode(),
				nodeVar));

		Op op = new OpBGP(bp);
		
		// GROUPBY
		VarExprList groupVars = new VarExprList();
		groupVars.add(nodeVar);
		List<ExprAggregator> aggregators = new LinkedList<ExprAggregator>();

		Aggregator countDistinctAggregator = new AggCountDistinct();
		aggregators.add(new ExprAggregator(scoreVar, countDistinctAggregator));
		op = new OpGroup(op, groupVars, aggregators);

		// EXTEND (not sure why this is necessary) TODO
		VarExprList exprs = new VarExprList();
		exprs.add(v1, scoreExpr);
		op = OpExtend.extend(op, exprs);

		// SELECT
		List<Var> vars = new LinkedList<Var>();
		vars.add(nodeVar);
		vars.add(scoreVar);
		op = new OpProject(op, vars);

		return op;
	}
	
	// public methods

	public void init(SparqlService sparqlService) {
		this.sparqlService = sparqlService;
		this.runQuery();
		this.setReady(true);
	}

	protected void runQuery() {

		Op edgeOp = getFullOp();
		Op scoreOp = getScoreOp();
		this.facetSimilarityGraph_ = sparqlService.runFacetQuery(edgeOp, scoreOp);
	}

	
    /**
     * A listener for the slide end event in a Primefaces Slider. This method
     * should NOT BE HERE!
     * TODO move onSlideEnd method into the controller
     * @param event
     */
    public void onSlideEnd(SlideEndEvent event) {  
    	this.setWeight(event.getValue()/100.0D); 
    }

	public boolean isCached() {
		return cached_;
	}

	public void setCached(boolean cached) {
		this.cached_ = cached;
	}  
	
	@Override
	public void update(Observable o, Object arg) {
		runQuery();
		
	}

}
