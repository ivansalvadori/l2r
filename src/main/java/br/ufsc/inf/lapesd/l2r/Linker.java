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
	private Map<String, Set<Resource>> index = new HashMap<>();

	public Model linkModel(Model model) {
		this.createIndex(model);
		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		linkedModel = this.convertLiteralToResource(model, model);
		return linkedModel;
	}

	public Model linkModel(Model resourceModel, Model... backgroundModel) {
		this.createIndex(backgroundModel);
		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		linkedModel = this.convertLiteralToResource(resourceModel, backgroundModel);
		return linkedModel;
	}

	private Model convertLiteralToResource(Model resourceModel, Model... backgroundModel) {
		Model convertedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		List<Resource> targetSubjects = resourceModel.listSubjects().toList();
		for (Resource resource : targetSubjects) {
			List<Statement> properties = resource.listProperties().toList();
			for (Statement statement : properties) {
				RDFNode object = statement.getObject();
				if (object.isLiteral()) {
					if (statement.getPredicate().equals(RDFS.label)) {
						convertedModel.add(statement);
						continue;
					}
					String literalValue = object.asLiteral().toString().toLowerCase();
					if (index.get(literalValue) != null) {
						List<Resource> backGroundResources = new ArrayList<>(index.get(literalValue));
						for (Resource backGroundResource : backGroundResources) {
							if (this.contextualizable != null) {
								contextualizable.createIndex(backgroundModel);
								Set<Resource> resolvedContexts = this.contextualizable.resolveContext(statement.getPredicate());
								if (resolvedContexts != null) {
									boolean hasTheCorrectContext = hasTheCorrectContext(backGroundResource, resolvedContexts, backgroundModel);
									if (hasTheCorrectContext) {
										convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
									}
								} else {
									convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
								}
							} else {
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

	private void createIndex(Model... backgroundModel) {
		this.index = new HashMap<>();
		for (Model model : backgroundModel) {
			List<Resource> backgroundSubjects = model.listSubjects().toList();
			for (Resource resource : backgroundSubjects) {
				if (resource.hasProperty(RDFS.label)) {
					List<Statement> labelValues = resource.listProperties(RDFS.label).toList();
					for (Statement statement : labelValues) {
						String labelValue = statement.getObject().asLiteral().toString().toLowerCase();
						Set<Resource> set = this.index.get(labelValue);
						if (set == null) {
							this.index.put(labelValue, set = new HashSet<Resource>());
						}
						set.add(resource);
					}
				}
			}
		}
	}

	private boolean hasTheCorrectContext(Resource backGroundResource, Set<Resource> resolvedContexts, Model... backgroundModel) {
		for (Model model : backgroundModel) {
			Resource resource = model.getResource(backGroundResource.getURI());
			if (resource != null) {
				Statement resourceStm = resource.getProperty(RDF.type);
				if (resourceStm != null) {
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
