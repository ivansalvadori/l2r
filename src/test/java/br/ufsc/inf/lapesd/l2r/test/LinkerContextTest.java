package br.ufsc.inf.lapesd.l2r.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;

import br.ufsc.inf.lapesd.l2r.L2RDataMgr;
import br.ufsc.inf.lapesd.l2r.Linker;

public class LinkerContextTest {

	Linker linker = new Linker();

	@Test
	public void linkerNoConflictTest() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(model, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, model, linker);

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");
		Statement statementToTest = resultedResource.getProperty(propertyToTest);

		Resource resultedObject = statementToTest.getObject().asResource();
		Resource expectedObject = linkedModel.getResource("http://example.com/background/Blue");
		Assert.assertEquals(resultedObject, expectedObject);
	}

	@Test
	public void linkerMultiModelsMultiResourcesConflictTest() throws IOException {
		Model colorModel = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/multiModels/colors.ttl")) {
			RDFDataMgr.read(colorModel, in, Lang.TURTLE);
		}

		Model carsModel = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/multiModels/cars.ttl")) {
			RDFDataMgr.read(carsModel, in, Lang.TURTLE);
		}

		Model rockbandsModel = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/multiModels/rockbands.ttl")) {
			RDFDataMgr.read(rockbandsModel, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(colorModel, linker);
		L2RDataMgr.index(carsModel, linker);
		L2RDataMgr.index(rockbandsModel, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, carsModel, linker);

		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		List<Statement> resultedResources = resultedResource.listProperties(propertyToTest).toList();
		Resource expectedObject = linkedModel.getResource("http://example.com/Color/Blue");

		Assert.assertTrue(resultedResources.size() == 1);
		Resource resultedObject = resultedResources.get(0).getObject().asResource();
		Assert.assertTrue(expectedObject.equals(resultedObject));
	}

	@Test
	public void linkerMultiModelsMultiResourcesSingleConflitSolvedTest() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/singleConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(model, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, model, linker);

		RDFDataMgr.write(System.out, linkedModel, Lang.TURTLE);

		Property propertyToTest = linkedModel.createProperty("http://onto.com/color");
		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		Resource resultedObject = resultedResource.getProperty(propertyToTest).getObject().asResource();
		Resource expectedObject = linkedModel.getResource("http://example.com/Color/Blue");

		Assert.assertTrue(expectedObject.equals(resultedObject));
	}

	@Test
	public void linkerBackgroundMultilabelTargetSingleLiteralTest() throws IOException {
		Model modelWithSeveralLabels = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/backgroundMultilabelTargetSingleLiteral.ttl")) {
			RDFDataMgr.read(modelWithSeveralLabels, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(modelWithSeveralLabels, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, modelWithSeveralLabels, linker);

		RDFDataMgr.write(System.out, linkedModel, Lang.TURTLE);

		Property propertyToTest = linkedModel.createProperty("http://l2r.com/sameProperty");

		Resource resultedObject0 = linkedModel.getResource("http://resource.com/0").getProperty(propertyToTest).getObject().asResource();
		Resource resultedObject1 = linkedModel.getResource("http://resource.com/1").getProperty(propertyToTest).getObject().asResource();
		Resource resultedObject2 = linkedModel.getResource("http://resource.com/2").getProperty(propertyToTest).getObject().asResource();
		Resource resultedObject3 = linkedModel.getResource("http://resource.com/3").getProperty(propertyToTest).getObject().asResource();

		Resource expectedObject = linkedModel.getResource("http://background.com/aConcept");

		Assert.assertEquals(resultedObject0, expectedObject);
		Assert.assertEquals(resultedObject1, expectedObject);
		Assert.assertEquals(resultedObject2, expectedObject);
		Assert.assertEquals(resultedObject3, expectedObject);
	}

	@Test
	public void linkerBackgroundSinglelabelTargetMultiLiteralTest() throws IOException {
		Model modelWithSeveralLabels = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/backgroundSinglelabelTargetMultiLiteral.ttl")) {
			RDFDataMgr.read(modelWithSeveralLabels, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(modelWithSeveralLabels, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, modelWithSeveralLabels, linker);

		RDFDataMgr.write(System.out, linkedModel, Lang.TURTLE);

		Property propertyToTest = linkedModel.createProperty("http://l2r.com/sameProperty");
		Resource resourceToTest = linkedModel.getResource("http://resource.com/0");

		List<Resource> expectedResourceList = new ArrayList<>();
		expectedResourceList.add(linkedModel.getResource("http://background.com/concept0"));
		expectedResourceList.add(linkedModel.getResource("http://background.com/concept1"));
		expectedResourceList.add(linkedModel.getResource("http://background.com/concept2"));

		List<Statement> resultedResources = resourceToTest.listProperties(propertyToTest).toList();
		for (Statement statement : resultedResources) {
			Resource resultedObject = statement.getObject().asResource();
			Assert.assertTrue(expectedResourceList.contains(resultedObject));
		}
	}

	@Test
	public void linkertNoResourceToReplace() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noResourceToReplace.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(model, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, model, linker);

		RDFDataMgr.write(System.out, linkedModel, Lang.TURTLE);

		Model expectedModel = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noResourceToReplace.ttl")) {
			RDFDataMgr.read(expectedModel, in, Lang.TURTLE);
		}
		Assert.assertTrue(linkedModel.isIsomorphicWith(expectedModel));
	}

	@Test
	public void linkerSaoPauloVSPalmeiras() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/timeECidade.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		linker.contextEnabled(true);
		L2RDataMgr.index(model, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, model, linker);
		RDFDataMgr.write(System.out, linkedModel, Lang.TURTLE);

		Resource resultedResource1 = linkedModel.getResource("http://pessoa.com/001");

		Property propertyToTest1 = linkedModel.createProperty("http://onto.com/cidadeNatal");
		List<Statement> resultedResources1 = resultedResource1.listProperties(propertyToTest1).toList();

		Property propertyToTest2 = linkedModel.createProperty("http://onto.com/torcePara");
		List<Statement> resultedResources2 = resultedResource1.listProperties(propertyToTest2).toList();

		Assert.assertTrue(resultedResources1.size() == 1);

		Resource expectedObject0 = linkedModel.getResource("http://example.com/cidade/SaoPaulo");
		Resource expectedObject1 = linkedModel.getResource("http://example.com/time/SaoPaulo");

		Resource resultedObject0 = resultedResources1.get(0).getObject().asResource();
		Resource resultedObject1 = resultedResources2.get(0).getObject().asResource();

		Assert.assertTrue(expectedObject0.equals(resultedObject0));
		Assert.assertTrue(expectedObject1.equals(resultedObject1));

	}
}
