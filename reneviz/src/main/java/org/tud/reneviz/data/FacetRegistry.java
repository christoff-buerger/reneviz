package org.tud.reneviz.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.tud.reneviz.service.facet.Facet;
import org.tud.reneviz.service.facet.SourcePropertyFacet;
import org.tud.reneviz.service.facet.TargetPropertyFacet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * This is a CDI bean that offers a registry for {@link Facet}.
 */
@ApplicationScoped
public class FacetRegistry {

	private Map<Resource, List<Facet>> facets;

	public FacetRegistry() {
		this.facets = new HashMap<Resource, List<Facet>>();

		this.addFacets();
	}

	public void addFacet(Resource topic, Facet facet) {
		if (!facets.containsKey(topic)) {
			List<Facet> newFacets = new LinkedList<Facet>();
			newFacets.add(facet);
			facets.put(topic, newFacets);
		} else {
			facets.get(topic).add(facet);
		}
	}

	private void addFacets() {
		Resource person = ResourceFactory
				.createResource("http://xmlns.com/foaf/0.1/Person");
		Resource organization = ResourceFactory
				.createResource("http://xmlns.com/foaf/0.1/Organization");
		Resource project = ResourceFactory
				.createResource("http://vivoweb.org/ontology/core#Project");
		Resource conference = ResourceFactory
				.createResource("http://purl.org/ontology/bibo/Conference");
		Resource workshop = ResourceFactory
				.createResource("http://purl.org/ontology/bibo/Workshop");
		Resource facultyMember = ResourceFactory
				.createResource("http://vivoweb.org/ontology/core#FacultyMember");
		Resource grant = ResourceFactory
				.createResource("http://vivoweb.org/ontology/core#Grant");
		Resource event = ResourceFactory
				.createResource("http://purl.org/NET/c4dm/event.owl#Event");
		
		
//		PREFIX vitro-public: <http://vitro.mannlib.cornell.edu/ns/vitro/public#>
//		PREFIX c4o: <http://purl.org/spar/c4o/>
//		PREFIX ero: <http://purl.obolibrary.org/obo/>
//		PREFIX pvs: <http://vivoweb.org/ontology/provenance-support#>
//		PREFIX owl2: <http://www.w3.org/2006/12/owl2-xml#>
//		PREFIX scirr: <http://vivoweb.org/ontology/scientific-research-resource#>
//		PREFIX vivo: <http://vivoweb.org/ontology/core#>
//		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//		PREFIX bibo: <http://purl.org/ontology/bibo/>
//		PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#>
//		PREFIX foaf: <http://xmlns.com/foaf/0.1/>
//		PREFIX scires: <http://vivoweb.org/ontology/scientific-research#>
//		PREFIX dcterms: <http://purl.org/dc/terms/>
//		PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
//		PREFIX geo: <http://aims.fao.org/aos/geopolitical.owl#>
//		PREFIX skco: <http://www.w3.org/2004/02/skos/core#>
//		PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
//		PREFIX dcelem: <http://purl.org/dc/elements/1.1/>
//		PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>
//		PREFIX vann: <http://purl.org/vocab/vann/>
//		PREFIX skos: <http://www.w3.org/2008/05/skos#>
//		PREFIX swvs: <http://www.w3.org/2003/06/sw-vocab-status/ns#>
//		PREFIX owl: <http://www.w3.org/2002/07/owl#>
//		PREFIX fabio: <http://purl.org/spar/fabio/>
//		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
//		 
//		# specific cornell prefixes
//		PREFIX vivo-hr: <http://vivo.cornell.edu/ns/hr/0.9/hr.owl#>
//		PREFIX vivo-nhr: <http://vivoweb.org/ontology/newhr#>
//		PREFIX vivo-cuo: <http://vivoweb.org/ontology/cu-vivo-osp#>
//		PREFIX vivo-ai: <http://vivoweb.org/ontology/activity-insight#>
//		PREFIX vitro-mann: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>
//		PREFIX vitro-mr: <http://vitro.mannlib.cornell.edu/ns/reporting#>
//		PREFIX vivo-coop: <http://vivoweb.org/ontology/cornell-cooperative-extension#>
//		PREFIX vivo-lib: <http://vivo.library.cornell.edu/ns/0.1#>
//		PREFIX vivo-ma: <http://vivo.cornell.edu/ns/mannadditions/0.1#>
//		PREFIX vivo-socsci: <http://vivo.library.cornell.edu/ns/vivo/socsci/0.1#>
//		PREFIX vitro-mann: <http://vitro.mannlib.cornell.edu/ns/bjl23/hr/1#>
		
		List<Property> plist = new ArrayList<Property>(2);


//		foaf:Person,SP0,vivo:hasResearchArea,-
//		foaf:Person,SP0,vivo-lib:memberOfGraduateField,-
//		foaf:Person,SP0,vivo-coop:hasPrimarySpecializationArea,-
//		foaf:Person,SP0,vivo:geographicFocus,-
//		foaf:Person,SP0,vivo-lib:affiliatedWithAsCornellFacultyMember,-
//		foaf:Person,SP0,vivo:featuredIn,-
//		foaf:Person,SP0,vivo:freetextKeyword,-
//		foaf:Person,SP0,vivo-ai:researchKeyword,-
//		foaf:Person,SP1,vivo:authorInAuthorship,vivo:linkedInformationResource
//		foaf:Person,SP1,vivo:personInPosition,vivo:positionInOrganization
//		foaf:Person,SP1,vivo:hasTeacherRole,vivo:roleRealizedIn
//		foaf:Person,SP1,vivo:authorInAuthorship,vivo:institutionalAffiliation
//		foaf:Person,SP1,vivo:hasMemberRole,vivo:roleContributesTo
		
		this.addFacet(person, new SourcePropertyFacet("Research Area", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasResearchArea"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Member of Graduate Field", person, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#memberOfGraduateField"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Primary Specialization Area", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/cornell-cooperative-extension#hasPrimarySpecializationArea"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Geographic Focus", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#geographicFocus"), 0.0D));
		
		this.addFacet(person, new SourcePropertyFacet("Member of Faculty", person, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#affiliatedWithAsCornellFacultyMember"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Featured In", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#featuredIn"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Freetext Keyword", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#freetextKeyword"), 0.0D));
		this.addFacet(person, new SourcePropertyFacet("Research Keyword", person, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#researchKeyword"), 0.0D));
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedInformationResource"));
		this.addFacet(person, new SourcePropertyFacet("Author of Ressource", person, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#positionInOrganization"));
		this.addFacet(person, new SourcePropertyFacet("Position in Organization", person, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasTeacherRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#roleRealizedIn"));
		this.addFacet(person, new SourcePropertyFacet("Teacher of Course", person, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#institutionalAffiliation"));
		this.addFacet(person, new SourcePropertyFacet("Affilitated with Institution", person, plist, 0.0D));
		
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasMemberRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#roleContributesTo"));
		this.addFacet(person, new SourcePropertyFacet("Member of Something", person, plist , 0.0D));
		


//		event:Event,SP0,vivo-lib:hasRegistrarSubjectArea,-
//		event:Event,SP0,vivo:hasGeographicLocation,-
//		event:Event,SP0,vivo:inEventSeries,-
//		event:Event,SP0,vivo-lib:topicDescription,-
//		event:Event,SP0,vivo-lib:eventHasHostPerson,-
//		event:Event,SP1,vivo:realizedRole,vivo:teacherRoleOf
//		event:Event,SP1,vivo:realizedRole,vivo:organizerRoleOf

		this.addFacet(event, new SourcePropertyFacet("Subject Area", event, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#hasRegistrarSubjectArea"), 0.0D));
		this.addFacet(event, new SourcePropertyFacet("Geographic Location", event, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasGeographicLocation"), 0.0D));
		this.addFacet(event, new SourcePropertyFacet("In Series", event, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#inEventSeries"), 0.0D));
		this.addFacet(event, new SourcePropertyFacet("Topic", event, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#topicDescription"), 0.0D));
		this.addFacet(event, new SourcePropertyFacet("Host Person", event, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#eventHasHostPerson"), 0.0D));
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#teacherRoleOf"));
		this.addFacet(event, new SourcePropertyFacet("Teacher in Event", event, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#organizerRoleOf"));
		this.addFacet(event, new SourcePropertyFacet("Organizer of Event", event, plist, 0.0D));
		
//		foaf:Organization,SP0,vivo:offersDegree,-
//		foaf:Organization,SP0,vivo:addressState,-
//		foaf:Organization,SP0,vivo-ai:location,-
//		foaf:Organization,SP0,vivo-ai:fundingAgentFor,-
//		foaf:Organization,SP0,vivo-lib:hasAffiliatedCornellFacultyMember,-
//		foaf:Organization,SP0,vivo:addressCity,-
//		foaf:Organization,SP0,vivo:partOf,-
//		foaf:Organization,SP1,vivo:organizationForPosition,vivo:positionForPerson

		this.addFacet(organization, new SourcePropertyFacet("Offers Degree", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#offersDegree"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("State", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#addressState"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("Location", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#location"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("Funds", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#fundingAgentFor"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("Affiliated Faculty Member", organization, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#hasAffiliatedCornellFacultyMember"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("City", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#addressCity"), 0.0D));
		this.addFacet(organization, new SourcePropertyFacet("Part of", organization, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#partOf"), 0.0D));

		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#organizationForPosition"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#positionForPerson"));
		this.addFacet(organization, new SourcePropertyFacet("Person in Organization", organization, plist, 0.0D));	
		
//		bibo:Conference,SP1,vivo:realizedRole,vivo:organizerRoleOf
//		bibo:Conference,SP1,vivo:includesEvent,dcterms:creator
//		bibo:Conference,SP1,vivo:includesEvent,vivo:hasPublicationVenue
	
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#organizerRoleOf"));
		this.addFacet(conference, new SourcePropertyFacet("Organizer of Cenference", conference, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#includesEvent"));
		plist.add(ResourceFactory.createProperty("http://purl.org/dc/terms/creator"));
		this.addFacet(conference, new SourcePropertyFacet("Creator of Event at Conference", conference, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#includesEvent"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasPublicationVenue"));
		this.addFacet(conference, new SourcePropertyFacet("Venue of Event at Conference", conference, plist, 0.0D));

//		vivo:FacultyMember,SP0,vivo-lib:memberOfGraduateField,-
//		vivo:FacultyMember,SP0,vivo:hasResearchArea,-
//		vivo:FacultyMember,SP0,vivo:geographicFocus,-
//		vivo:FacultyMember,SP0,vivo:featuredIn,-
//		vivo:FacultyMember,SP0,vivo-coop:hasPrimarySpecializationArea,-
//		vivo:FacultyMember,SP0,vivo-ai:hasConcentrationSubjectArea,-
//		vivo:FacultyMember,SP0,vivo:freetextKeyword,-
//		vivo:FacultyMember,SP0,vivo-ai:researchKeyword,-
//		vivo:FacultyMember,SP1,vivo:hasTeacherRole,vivo:roleRealizedIn
//		vivo:FacultyMember,SP1,vivo:personInPosition,vivo:positionInOrganization

		this.addFacet(facultyMember, new SourcePropertyFacet("Member of Graduate Field", facultyMember, ResourceFactory.createProperty("http://vivo.library.cornell.edu/ns/0.1#memberOfGraduateField"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Research Area", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasResearchArea"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Geographic Focus", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#geographicFocus"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Featured In", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#featuredIn"), 0.0D));
		
		this.addFacet(facultyMember, new SourcePropertyFacet("Primary Specialization Area", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/cornell-cooperative-extension#hasPrimarySpecializationArea"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Concentration Area", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#hasConcentrationSubjectArea"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Freetext Keyword", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#freetextKeyword"), 0.0D));
		this.addFacet(facultyMember, new SourcePropertyFacet("Research Keyword", facultyMember, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#researchKeyword"), 0.0D));
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#personInPosition"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#positionInOrganization"));
		this.addFacet(facultyMember, new SourcePropertyFacet("Author of Ressource", facultyMember, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasTeacherRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#roleRealizedIn"));
		this.addFacet(facultyMember, new SourcePropertyFacet("Teacher of Course", facultyMember, plist, 0.0D));

//		vivo:Grant,SP0,vivo:grantAwardedBy,-
//		vivo:Grant,SP0,vivo:administeredBy,-
//		vivo:Grant,TP1,vivo:roleContributesTo,vivo:hasPrincipalInvestigatorRole

		this.addFacet(grant, new SourcePropertyFacet("Awarded by", grant, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#grantAwardedBy"), 0.0D));
		this.addFacet(grant, new SourcePropertyFacet("Administered by", grant, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#administeredBy"), 0.0D));

		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasPrincipalInvestigatorRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#roleContributesTo"));
		this.addFacet(grant, new TargetPropertyFacet("Principal Investigator", grant, plist, 0.0D));

//		vivo:Project,SP0,vivo:domesticGeographicFocus,-
//		vivo:Project,SP0,vivo-ai:hasContributionArea,-
//		vivo:Project,SP0,vivo-ai:fundedByAgent,-
//		vivo:Project,SP0,vivo-ai:submittedBy,-
//		vivo:Project,SP1,vivo:realizedRole,vivo:researcherRoleOf
//		vivo:Project,SP1,vivo:realizedRole,vivo-ai:partnerRoleOf

		this.addFacet(project, new SourcePropertyFacet("Geographic Focus", project, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#domesticGeographicFocus"), 0.0D));
		this.addFacet(project, new SourcePropertyFacet("Contribution Area", project, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#hasContributionArea"), 0.0D));
		this.addFacet(project, new SourcePropertyFacet("Funding Agent", project, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#fundedByAgent"), 0.0D));
		this.addFacet(project, new SourcePropertyFacet("Submitter", project, ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#submittedBy"), 0.0D));
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#researcherRoleOf"));
		this.addFacet(project, new SourcePropertyFacet("Researcher", project, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/activity-insight#partnerRoleOf"));
		this.addFacet(project, new SourcePropertyFacet("Partner", project, plist, 0.0D));
		
//		bibo:Workshop,SP0,vivo:hasGeographicLocation,-
//		bibo:Workshop,SP0,vivo:inEventSeries,-
//		bibo:Workshop,SP1,vivo:realizedRole,vivo:organizerRoleOf
//		bibo:Workshop,SP1,vivo:realizedRole,vivo:teacherRoleOf		

		this.addFacet(workshop, new SourcePropertyFacet("Geographic Location", workshop, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#hasGeographicLocation"), 0.0D));
		this.addFacet(workshop, new SourcePropertyFacet("In Event Series", workshop, ResourceFactory.createProperty("http://vivoweb.org/ontology/core#inEventSeries"), 0.0D));
		
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#organizerRoleOf"));
		this.addFacet(workshop, new SourcePropertyFacet("Organizer", workshop, plist, 0.0D));
		
		plist = new ArrayList<Property>(2);
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#realizedRole"));
		plist.add(ResourceFactory.createProperty("http://vivoweb.org/ontology/core#teacherRoleOf"));
		this.addFacet(workshop, new SourcePropertyFacet("Teacher in Workshop", workshop, plist, 0.0D));
	}

	public List<Facet> getFacets(Resource topic) {
		if (facets.containsKey(topic)) {
			return facets.get(topic);
		} else {
			return Collections.emptyList();
		}

	}
}
