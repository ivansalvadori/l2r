package br.ufsc.inf.lapesd.l2r;

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

public interface Contextualizable {
	
    Set<Property> resolveContext(Property Property, Model... backgroundModel);


}
