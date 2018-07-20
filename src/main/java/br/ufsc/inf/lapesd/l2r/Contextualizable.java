package br.ufsc.inf.lapesd.l2r;

import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public interface Contextualizable {
	
    Set<Property> resolveContext(Property Property, Map<String, Set<Resource>> backGroundResourceIndex);


}
