package br.ufsc.inf.lapesd.l2r.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.junit.Test;

import br.ufsc.inf.lapesd.l2r.L2RDataMgr;
import br.ufsc.inf.lapesd.l2r.L2RIndexerInputHandler;
import br.ufsc.inf.lapesd.l2r.L2RLinkerInputHandler;
import br.ufsc.inf.lapesd.l2r.Linker2;

public class StreamRDFtest {

	@Test
	public void testResolveContextNoConflict() throws IOException {

		// create index
		L2RIndexerInputHandler indexerHandler = new L2RIndexerInputHandler();
		Linker2 linker = new Linker2();
		indexerHandler.setLinker(linker);
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.parse(indexerHandler, in, Lang.TURTLE);
		}

		// link a rdf resource
		try (FileOutputStream fileOutputStream = new FileOutputStream("teste.ttl")) {
			final StreamRDF outputHandler = StreamRDFWriter.getWriterStream(fileOutputStream, Lang.TURTLE);
			L2RLinkerInputHandler linkerHandler = new L2RLinkerInputHandler();
			linkerHandler.setOutputHandler(outputHandler);
			linkerHandler.setLinker(linker);
			try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
				RDFDataMgr.parse(linkerHandler, in, Lang.TURTLE);
			}
			outputHandler.finish();
		}

		Model convertedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		StreamRDF dest = StreamRDFLib.graph(convertedModel.getGraph());

		L2RLinkerInputHandler linkerHandler = new L2RLinkerInputHandler();
		linkerHandler.setOutputHandler(dest);
		linkerHandler.setLinker(linker);
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.parse(linkerHandler, in, Lang.TURTLE);
		}
		dest.finish();

		System.out.println(convertedModel);

	};

	@Test
	public void xx() {

		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = getClass().getResourceAsStream("/linker/noConflict.ttl")) {
			RDFDataMgr.read(model, in, Lang.TURTLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Linker2 linker = new Linker2();

		L2RDataMgr.index(model, linker, Lang.TURTLE);

		System.out.println(linker.index);

	}
}
