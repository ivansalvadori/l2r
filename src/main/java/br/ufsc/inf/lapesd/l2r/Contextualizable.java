package br.ufsc.inf.lapesd.l2r;

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface Contextualizable {

	Set<Resource> resolveContext(Resource property);

	void createIndex(Model... backgroundModel);

}
