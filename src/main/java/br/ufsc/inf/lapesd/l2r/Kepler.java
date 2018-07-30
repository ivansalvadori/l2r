package br.ufsc.inf.lapesd.l2r;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class Kepler implements Contextualizable {

	private Map<Resource, Set<Resource>> contextualIndex = new HashMap<>();

	@Override
	public Set<Resource> resolveContext(Resource property) {
		Set<Resource> context = contextualIndex.get(property);
		return context;
	}

	@Override
	public void createIndex(Model... backgroundModel) {
		for (Model model : backgroundModel) {
			List<Resource> backgroundSubjects = model.listSubjects().toList();
			for (Resource resource : backgroundSubjects) {
				List<Statement> list = resource.listProperties().toList();
				for (Statement resourceProperty : list) {
					Property predicate = resourceProperty.getPredicate();
					RDFNode object = resourceProperty.getObject();
					if (object.isResource() && !predicate.equals(RDF.type)) {
						Resource resourcePropertyRange = getResourcePropertyRange(object.asResource(), backgroundModel);
						if (resourcePropertyRange == null) {
							continue;
						}
						if (this.contextualIndex.get(predicate) == null) {
							contextualIndex.put(predicate, new HashSet<>());
						}
						this.contextualIndex.get(predicate).add(resourcePropertyRange);
					}
				}
			}
		}
	}

	private Resource getResourcePropertyRange(Resource object, Model... backgroundModel) {
		for (Model model : backgroundModel) {
			Resource resource = model.getResource(object.getURI());
			Statement propertyType = resource.getProperty(RDF.type);
			if (resource != null && propertyType != null) {
				return propertyType.getObject().asResource();
			}
		}
		return null;
	}

}
