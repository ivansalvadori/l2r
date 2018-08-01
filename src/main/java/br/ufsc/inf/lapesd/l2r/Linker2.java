package br.ufsc.inf.lapesd.l2r;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Linker2 {

	private Contextualizable contextualizable;
	/*
	 * Map of <literalObject, resourceURI(resource.label=literalObject)>
	 */
	private Map<String, Set<String>> index = new HashMap<>();

	public StreamRDF link(StreamRDF rdfSource) {
		System.out.println(this.index);
		return null;
	}

	public void index(Triple triple) {
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();
		if (predicate.getURI().equals(RDFS.label.getURI())) {
			Set<String> resourceURIsofTheLabel = this.index.get(object.toString());
			if (resourceURIsofTheLabel == null) {
				resourceURIsofTheLabel = new HashSet<>();
			}
			resourceURIsofTheLabel.add(subject.getURI());
			this.index.put(object.getLiteralValue().toString().toLowerCase(), resourceURIsofTheLabel);
		}
	}

	public Set<Triple> link(Triple triple) {
		Set<Triple> linkedTriples = new HashSet<>();
		Node object = triple.getObject();
		if (object.isLiteral()) {
			if (triple.getPredicate().getURI().equals(RDFS.label.getURI())) {
				linkedTriples.add(triple);
				return linkedTriples;
			}
			Set<String> convertedObject = this.index.get(object.getLiteral().toString().toLowerCase());
			if (convertedObject != null) {
				for (String uri : convertedObject) {
					Node newObject = NodeFactory.createURI(uri);
					Triple linkedTriple = Triple.create(triple.getSubject(), triple.getPredicate(), newObject);
					linkedTriples.add(linkedTriple);
				}
			} else {
				linkedTriples.add(triple);
			}
		} else {
			linkedTriples.add(triple);
		}

		return linkedTriples;
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
