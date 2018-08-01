package br.ufsc.inf.lapesd.l2r.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import br.ufsc.inf.lapesd.l2r.Linker2;

public class LinkerNoContextTest {

	@Test
	public void linkerNoConflictTestModelToModel() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}

		Linker2 linker = new Linker2();
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
	public void linkerNoConflictTestFileToModel() throws IOException {
		Linker2 linker = new Linker2();
		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			L2RDataMgr.index(in, Lang.TURTLE, linker);
		}

		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			L2RDataMgr.link(linkedModel, in, Lang.TURTLE, linker);
		}

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");
		Statement statementToTest = resultedResource.getProperty(propertyToTest);
		Resource resultedObject = statementToTest.getObject().asResource();
		Resource expectedObject = linkedModel.getResource("http://example.com/background/Blue");
		Assert.assertEquals(resultedObject, expectedObject);
	}

	@Test
	public void linkerNoConflictTestModelToFile() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		}
		Linker2 linker = new Linker2();
		L2RDataMgr.index(model, linker);

		try (FileOutputStream fileOutputStream = new FileOutputStream("test.ttl")) {
			L2RDataMgr.link(fileOutputStream, Lang.TURTLE, model, linker);
		}

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try (InputStream in = new FileInputStream("test.ttl")) {
			RDFDataMgr.read(linkedModel, in, Lang.TURTLE);
		}

		File file = new File("test.ttl");
		if (file.exists()) {
			file.delete();
		}

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");
		Statement statementToTest = resultedResource.getProperty(propertyToTest);
		Resource resultedObject = statementToTest.getObject().asResource();
		Resource expectedObject = linkedModel.getResource("http://example.com/background/Blue");
		Assert.assertEquals(resultedObject, expectedObject);
	}

	@Test
	public void linkerNoConflictTestFileToFile() throws IOException {
		Linker2 linker = new Linker2();

		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			L2RDataMgr.index(in, Lang.TURTLE, linker);
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream("test.ttl")) {
			try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
				L2RDataMgr.link(fileOutputStream, Lang.TURTLE, in, Lang.TURTLE, linker);
			}
		}

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try (InputStream in = new FileInputStream("test.ttl")) {
			RDFDataMgr.read(linkedModel, in, Lang.TURTLE);
		}

		File file = new File("test.ttl");
		if (file.exists()) {
			file.delete();
		}

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");
		Statement statementToTest = resultedResource.getProperty(propertyToTest);
		Resource resultedObject = statementToTest.getObject().asResource();
		Resource expectedObject = linkedModel.getResource("http://example.com/background/Blue");
		Assert.assertEquals(resultedObject, expectedObject);

	}

	@Test
	public void linkerMultiModelsMultiResourcesTest() throws IOException {

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

		Linker2 linker = new Linker2();
		L2RDataMgr.index(rockbandsModel, linker);
		L2RDataMgr.index(colorModel, linker);
		L2RDataMgr.index(carsModel, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, carsModel, linker);

		Property propertyToTest = linkedModel.createProperty("http://cars.com/color");

		Resource resultedResource = linkedModel.getResource("http://cars.com/001");
		List<Statement> resultedResources = resultedResource.listProperties(propertyToTest).toList();
		Resource expectedObject0 = linkedModel.getResource("http://example.com/Color/Blue");
		Resource expectedObject1 = linkedModel.getResource("http://example.com/Rockband/Blue");

		Assert.assertTrue(resultedResources.size() == 2);

		Resource resultedObject0 = resultedResources.get(0).getObject().asResource();
		Resource resultedObject1 = resultedResources.get(1).getObject().asResource();

		Assert.assertTrue(expectedObject0.equals(resultedObject0) || expectedObject0.equals(resultedObject1));
		Assert.assertTrue(expectedObject1.equals(resultedObject0) || expectedObject1.equals(resultedObject1));

	}

	@Test
	public void linkerBackgroundMultilabelTargetSingleLiteralTest() throws IOException {
		Model modelWithSeveralLabels = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/backgroundMultilabelTargetSingleLiteral.ttl")) {
			RDFDataMgr.read(modelWithSeveralLabels, in, Lang.TURTLE);
		}

		Linker2 linker = new Linker2();
		L2RDataMgr.index(modelWithSeveralLabels, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, modelWithSeveralLabels, linker);

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

		Linker2 linker = new Linker2();
		L2RDataMgr.index(modelWithSeveralLabels, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, modelWithSeveralLabels, linker);

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

		Linker2 linker = new Linker2();
		L2RDataMgr.index(model, linker);

		Model linkedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		L2RDataMgr.link(linkedModel, model, linker);

		Model expectedModel = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noResourceToReplace.ttl")) {
			RDFDataMgr.read(expectedModel, in, Lang.TURTLE);
		}
		Assert.assertTrue(linkedModel.isIsomorphicWith(expectedModel));
	}

}
