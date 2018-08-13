package br.ufsc.inf.lapesd.l2r;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.riot.system.StreamRDFWriter;

public class L2RDataMgr {

	synchronized public static void index(Model model, Linker linker) {
		L2RIndexerHandler indexerHandler = new L2RIndexerHandler();
		indexerHandler.setLinker(linker);

		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, model, Lang.TURTLE);

		String serializedModel = out.toString();
		InputStream in;
		try {
			in = IOUtils.toInputStream(serializedModel, "UTF-8");
			RDFDataMgr.parse(indexerHandler, in, Lang.TURTLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	synchronized public static void index(Model model, Charset modelCharset, Linker linker) {
		L2RIndexerHandler indexerHandler = new L2RIndexerHandler();
		indexerHandler.setLinker(linker);

		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, model, Lang.TURTLE);

		String serializedModel = out.toString();
		try (InputStream in = IOUtils.toInputStream(serializedModel, modelCharset)) {
			RDFDataMgr.parse(indexerHandler, in, Lang.TURTLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	synchronized public static void index(InputStream in, Lang lang, Linker linker) {
		L2RIndexerHandler indexerHandler = new L2RIndexerHandler();
		indexerHandler.setLinker(linker);
		RDFDataMgr.parse(indexerHandler, in, lang);
	}

	synchronized public static void index(StringReader in, Lang lang, Linker linker) {
		L2RIndexerHandler indexerHandler = new L2RIndexerHandler();
		indexerHandler.setLinker(linker);
		RDFDataMgr.parse(indexerHandler, in, lang);
	}

	synchronized public static void link(Model destination, Model modelToLink, Linker linker) {
		StreamRDF output = StreamRDFLib.graph(destination.getGraph());
		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, modelToLink, Lang.TURTLE);
		String serializedModel = out.toString();
		InputStream in;
		try {
			in = IOUtils.toInputStream(serializedModel, "UTF-8");
			L2RLinkerHandler linkerHandler = new L2RLinkerHandler();
			linkerHandler.setOutputHandler(output);
			linkerHandler.setLinker(linker);
			RDFDataMgr.parse(linkerHandler, in, Lang.TURTLE);
			output.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	synchronized public static void link(OutputStream output, Lang lang, Model modelToLink, Linker linker) {
		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, modelToLink, Lang.TURTLE);
		String serializedModel = out.toString();
		InputStream in;
		try {
			in = IOUtils.toInputStream(serializedModel, "UTF-8");
			L2RLinkerHandler linkerHandler = new L2RLinkerHandler();
			final StreamRDF outputHandler = StreamRDFWriter.getWriterStream(output, lang);
			linkerHandler.setOutputHandler(outputHandler);
			linkerHandler.setLinker(linker);
			RDFDataMgr.parse(linkerHandler, in, Lang.TURTLE);
			outputHandler.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	synchronized public static void link(Model destination, InputStream in, Lang inputLang, Linker linker) {
		StreamRDF output = StreamRDFLib.graph(destination.getGraph());
		L2RLinkerHandler linkerHandler = new L2RLinkerHandler();
		linkerHandler.setOutputHandler(output);
		linkerHandler.setLinker(linker);
		RDFDataMgr.parse(linkerHandler, in, inputLang);
		output.finish();
	}

	synchronized public static void link(Model destination, StringReader in, Lang inputLang, Linker linker) {
		StreamRDF output = StreamRDFLib.graph(destination.getGraph());
		L2RLinkerHandler linkerHandler = new L2RLinkerHandler();
		linkerHandler.setOutputHandler(output);
		linkerHandler.setLinker(linker);
		RDFDataMgr.parse(linkerHandler, in, inputLang);
		output.finish();
	}

	synchronized public static void link(OutputStream output, Lang outputLang, InputStream in, Lang inputLang, Linker linker) {
		L2RLinkerHandler linkerHandler = new L2RLinkerHandler();
		final StreamRDF outputHandler = StreamRDFWriter.getWriterStream(output, outputLang);
		linkerHandler.setOutputHandler(outputHandler);
		linkerHandler.setLinker(linker);
		RDFDataMgr.parse(linkerHandler, in, inputLang);
		outputHandler.finish();
	}
}
