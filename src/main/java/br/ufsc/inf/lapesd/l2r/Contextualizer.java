package br.ufsc.inf.lapesd.l2r;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;

public class Contextualizer {

	private Index contextualIndex;
	private Index mapResourceURIResourceTypeIndex = new InMemoryIndex();;
	private Index mapPredicateResourceURI = new InMemoryIndex();;

	public void persistentIndex() {
		this.mapResourceURIResourceTypeIndex = new PersistentIndex("mapResourceURIResourceTypeIndex");
		this.mapPredicateResourceURI = new PersistentIndex("mapPredicateResourceURI");
	}

	public void addToIndex(Triple triple) {
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();
		this.indexResourceTypeIndex(subject, predicate, object);
		this.indexPredicateResourceURI(subject, predicate, object);
	}

	private void indexResourceTypeIndex(Node subject, Node predicate, Node object) {
		if (!object.isURI()) {
			return;
		}

		if (predicate.getURI().equals(RDF.type.getURI())) {
			Set<String> types = this.mapResourceURIResourceTypeIndex.load(object.getURI());
			if (types == null) {
				types = new HashSet<>();
			}
			types.add(object.getURI());

			for (String type : types) {
				this.mapResourceURIResourceTypeIndex.addToIndex(subject.getURI(), type);
			}
		}
	}

	private void indexPredicateResourceURI(Node subject, Node predicate, Node object) {
		if (!object.isURI()) {
			return;
		}

		if (predicate.getURI().equals(RDF.type.getURI())) {
			return;
		}

		Set<String> resourceURIs = this.mapPredicateResourceURI.load(predicate.getURI());
		if (resourceURIs == null) {
			resourceURIs = new HashSet<>();
		}
		resourceURIs.add(object.getURI());
		for (String uri : resourceURIs) {
			this.mapPredicateResourceURI.addToIndex(predicate.getURI(), uri);
		}
	}

	public Set<String> loadContext(String predicateURI) {
		Set<String> resourcesURIs = this.mapPredicateResourceURI.load(predicateURI);
		if (resourcesURIs == null) {
			return null;
		}

		Set<String> context = new HashSet<>();

		for (String resourceURI : resourcesURIs) {
			Set<String> resourceTypes = this.mapResourceURIResourceTypeIndex.load(resourceURI);
			if (resourceTypes == null) {
				return null;
			}
			for (String type : resourceTypes) {
				context.add(type);
			}
		}

		return context;
	}

	public Set<String> getType(String resourceURI) {
		Set<String> resourcesURIs = this.mapResourceURIResourceTypeIndex.load(resourceURI);
		if (resourcesURIs == null) {
			return null;
		}

		return this.mapResourceURIResourceTypeIndex.load(resourceURI);
	}

}
