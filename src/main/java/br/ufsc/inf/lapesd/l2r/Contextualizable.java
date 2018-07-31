package br.ufsc.inf.lapesd.l2r;

import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;

public interface Contextualizable {

	void index(Triple triple);

	void createContextualIndex();

	Set<Resource> resolveContext(String propertyURI);

}
