package br.ufsc.inf.lapesd.l2r.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import br.ufsc.inf.lapesd.l2r.Contextualizable;
import br.ufsc.inf.lapesd.l2r.Kepler;
import br.ufsc.inf.lapesd.l2r.Linker;

public class KeplerTest {

    @Test
    public void convertLiteralToResourceTest() throws IOException {

        Model colorModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/kepler/colors.ttl")) {
            RDFDataMgr.read(colorModel, in, Lang.TURTLE);
        }

        Model carsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/kepler/cars.ttl")) {
            RDFDataMgr.read(carsModel, in, Lang.TURTLE);
        }

        Model rockbandsModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/kepler/rockbands.ttl")) {
            RDFDataMgr.read(rockbandsModel, in, Lang.TURTLE);
        }
        
        Model bikesModel = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getResourceAsStream("/kepler/bikes.ttl")) {
            RDFDataMgr.read(bikesModel, in, Lang.TURTLE);
        }

       Contextualizable contextualizable = new Kepler();
       contextualizable.createIndex(colorModel, carsModel, rockbandsModel, bikesModel);

       Linker linker = new Linker();
       linker.setContextualizable(contextualizable);
       Model convertedModel = linker.convertLiteralToResource(carsModel, colorModel, rockbandsModel);
       RDFDataMgr.write(System.out, convertedModel, Lang.N3);
    }

}
