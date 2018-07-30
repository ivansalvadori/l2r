package br.ufsc.inf.lapesd.l2r.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;

import br.ufsc.inf.lapesd.l2r.Contextualizable;
import br.ufsc.inf.lapesd.l2r.Kepler;

public class KeplerTest {

	@Test
	public void testResolveContextNoConflict() throws IOException {

		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/kepler/noConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		Contextualizable contextualizable = new Kepler();
		contextualizable.createIndex(model);

		OntModel testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Property propertyToTest = testModel.createProperty("http://onto.com/color");
		Property expectedResource = testModel.createProperty("http://example.com/Color");

		Set<Resource> resultContext = contextualizable.resolveContext(propertyToTest);
		Assert.assertTrue(resultContext.contains(expectedResource));
	}

	@Test
	public void testResolveContextSingleConflict() throws IOException {

		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/kepler/singleConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		Contextualizable contextualizable = new Kepler();
		contextualizable.createIndex(model);

		OntModel testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Property propertyToTest = testModel.createProperty("http://onto.com/color");
		Property expectedResource = testModel.createProperty("http://example.com/Color");

		Set<Resource> resultContext = contextualizable.resolveContext(propertyToTest);
		Assert.assertTrue(resultContext.contains(expectedResource));
	}

	@Test
	public void testResolveContextNoResourceToReplace() throws IOException {

		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/kepler/noResourceToReplace.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		Contextualizable contextualizable = new Kepler();
		contextualizable.createIndex(model);

		OntModel testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Property propertyToTest = testModel.createProperty("http://onto.com/color");

		Set<Resource> resultContext = contextualizable.resolveContext(propertyToTest);
		Assert.assertNull(resultContext);
	}

	@Test
	public void testResolveContextSeveralContexts() throws IOException {

		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/kepler/severalContexts.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		Contextualizable contextualizable = new Kepler();
		contextualizable.createIndex(model);

		OntModel testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Property propertyToTest = testModel.createProperty("http://onto.com/color");
		Property expectedResource0 = testModel.createProperty("http://example.com/Color");
		Property expectedResource1 = testModel.createProperty("http://exemplo.com.br.com/Cor");

		Set<Resource> resultContext = contextualizable.resolveContext(propertyToTest);
		Assert.assertTrue(resultContext.contains(expectedResource0));
		Assert.assertTrue(resultContext.contains(expectedResource1));
	}

}
