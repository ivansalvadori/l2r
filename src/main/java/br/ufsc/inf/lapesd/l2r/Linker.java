package br.ufsc.inf.lapesd.l2r;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Linker {

	private Contextualizable contextualizable;

	public Model convertLiteralToResource(Model targetModel, Model... backgroundModel) {
		Model convertedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		convertedModel = this.convertResource(targetModel, backgroundModel);
		return convertedModel;
	}

	public Map<String, Set<Resource>> createIndex(Model... backgroundModel) {
		Map<String, Set<Resource>> mapLabelResourceUri = new HashMap<>();
		for (Model model : backgroundModel) {
			List<Resource> backgroundSubjects = model.listSubjects().toList();
			for (Resource resource : backgroundSubjects) {
				if (resource.hasProperty(RDFS.label)) {
					List<Statement> labelValues = resource.listProperties(RDFS.label).toList();
					for (Statement statement : labelValues) {
						String labelValue = statement.getObject().asLiteral().toString().toLowerCase();
						Set<Resource> set = mapLabelResourceUri.get(labelValue);
						if (set == null) {
							mapLabelResourceUri.put(labelValue, set = new HashSet<Resource>());
						}
						set.add(resource);
					}
				}
			}
		}
		return mapLabelResourceUri;
	}

	public Model convertResource(Model resourceModel,  Model... backgroundModel) {
		Model convertedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		
		Map<String, Set<Resource>> index = this.createIndex(backgroundModel);
		
		List<Resource> targetSubjects = resourceModel.listSubjects().toList();
		for (Resource resource : targetSubjects) {
			List<Statement> properties = resource.listProperties().toList();
			for (Statement statement : properties) {
				RDFNode object = statement.getObject();
				if (object.isLiteral()) {
					String literalValue = object.asLiteral().toString().toLowerCase();
					if (index.get(literalValue) != null) {
						List<Resource> backGroundResources = new ArrayList<>(index.get(literalValue));
						for (Resource backGroundResource : backGroundResources) {
							if(this.contextualizable != null) {
								Set<Resource> resolvedContexts = this.contextualizable.resolveContext(statement.getPredicate());
								if(resolvedContexts != null) {
										boolean hasTheCorrectContext = hasTheCorrectContext(backGroundResource, resolvedContexts, backgroundModel);
										if(hasTheCorrectContext) {
											convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
										}
								}
								else {
									convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
								}
							}
							else {
								convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
							}
						}
					} else {
						convertedModel.add(statement);
					}
				} else {
					convertedModel.add(statement);
				}
			}
		}
		return convertedModel;
	}
	
	boolean hasTheCorrectContext(Resource backGroundResource, Set<Resource> resolvedContexts, Model... backgroundModel) {
		for (Model model : backgroundModel) {
			Resource resource = model.getResource(backGroundResource.getURI());
			if(resource != null) {
				Statement resourceStm = resource.getProperty(RDF.type);
				if(resourceStm != null) {
					Resource object = resourceStm.getObject().asResource();
					return resolvedContexts.contains(object);
				}
			}
		}
		
		return false;
		
	}

	
	public void setContextualizable(Contextualizable contextualizable) {
		this.contextualizable = contextualizable;
	}
}
