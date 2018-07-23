package br.ufsc.inf.lapesd.l2r;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Kepler implements Contextualizable {

	@Override
	public Set<Property> resolveContext(Property Property, Model... backgroundModel) {
		this.createIndex(backgroundModel);
		return null;
	}

	private Map<Resource, Set<Resource>> createIndex(Model... backgroundModel) {
		Map<Resource, Set<Resource>> mapLabelResourceUri = new HashMap<>();
		for (Model model : backgroundModel) {
			List<Resource> backgroundSubjects = model.listSubjects().toList();
			for (Resource resource : backgroundSubjects) {
				List<Statement> list = resource.listProperties().toList();
				for (Statement resourceProperty : list) {
					Property predicate = resourceProperty.getPredicate();
					RDFNode object = resourceProperty.getObject();
					if(object.isResource() && !predicate.equals(RDF.type)) {
						System.out.println(predicate +": "+object);
						Resource resourcePropertyRange = getResourcePropertyRange(object.asResource(), backgroundModel);
						System.out.println(resourcePropertyRange);
					}
				}
			}
		}
		return mapLabelResourceUri;
	}
	
	private Resource getResourcePropertyRange(Resource object, Model... backgroundModel) {
		for (Model model : backgroundModel) {
			Resource resource = model.getResource(object.getURI());
			Statement propertyType = resource.getProperty(RDF.type);
			if(resource != null && propertyType!= null) {
				return propertyType.getObject().asResource();
			}
		}
		
		return null;
		
	}

}
