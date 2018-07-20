package br.ufsc.inf.lapesd.l2r.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import br.ufsc.inf.lapesd.l2r.Linker;

public class ConvertionTest {

    @Test
    public void convertLiteralToResourceTest() throws IOException {

        Model colorModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/colors.ttl")) {
            RDFDataMgr.read(colorModel, in, Lang.TURTLE);
        }

        Model carsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/cars.ttl")) {
            RDFDataMgr.read(carsModel, in, Lang.TURTLE);
        }

        Model rockbandsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/rockbands.ttl")) {
            RDFDataMgr.read(rockbandsModel, in, Lang.TURTLE);
        }

        Linker linker = new Linker();
        Model convertedModel = linker.convertLiteralToResource(carsModel, colorModel, rockbandsModel);
        RDFDataMgr.write(System.out, convertedModel, Lang.N3);

        // Assert.assertTrue(caught);
        // Assert.assertEquals(old, model.size());
    }

    @Test
    public void convertLiteralToResourcebasednOnSeparatedIndexTest() throws IOException {

        Model colorModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/colors.ttl")) {
            RDFDataMgr.read(colorModel, in, Lang.TURTLE);
        }

        Model carsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/cars.ttl")) {
            RDFDataMgr.read(carsModel, in, Lang.TURTLE);
        }

        Model rockbandsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/rockbands.ttl")) {
            RDFDataMgr.read(rockbandsModel, in, Lang.TURTLE);
        }

        Linker linker = new Linker();
        Map<String, Set<Resource>> index = linker.createIndex(colorModel, carsModel, rockbandsModel);

        Model convertedModel = linker.convertResource(carsModel, index);
        RDFDataMgr.write(System.out, convertedModel, Lang.N3);

        // Assert.assertTrue(caught);
        // Assert.assertEquals(old, model.size());
    }

    @Test
    public void mustConsiderSeveralLabels() throws IOException {

        Model modelWithSeveralLabels = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/Linker/severalLabels/resourceWithSeveralLabels.ttl")) {
            RDFDataMgr.read(modelWithSeveralLabels, in, Lang.TURTLE);
        }

        Linker linker = new Linker();
        linker.createIndex(modelWithSeveralLabels);
        
        Model convertedModel = linker.convertLiteralToResource(modelWithSeveralLabels, modelWithSeveralLabels);
        RDFDataMgr.write(System.out, convertedModel, Lang.N3);

        // Assert.assertTrue(caught);
        // Assert.assertEquals(old, model.size());
    }

}
