package br.ufsc.inf.lapesd.l2r;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class Kepler2 implements Contextualizable {

	private Map<String, Set<String>> contextualIndex = new HashMap<>();
	private Map<String, String> resourceTypeIndex = new HashMap<>();
	private Map<String, String> tempPredicate = new HashMap<>();
	private boolean inMemoryindex = true;

	@Override
	public Set<Resource> resolveContext(String propertyURI) {
		return null;
	}

	@Override
	public void index(Triple triple) {
		System.out.println(triple);
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();

		addToResourceTypeIndex(subject.getURI(), predicate.getURI(), object.getURI());
	}

	private void addToResourceTypeIndex(String resourceURI, String predicateURI, String resourceTypeURI) {
		if (predicateURI.equals(RDF.type.getURI())) {
			this.resourceTypeIndex.put(resourceURI, resourceTypeURI);
		}
	}

	public boolean isInMemoryindex() {
		return inMemoryindex;
	}

	@Override
	public void createContextualIndex() {

	}

}
