package br.ufsc.inf.lapesd.l2r;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class L2RDataMgr {

	public static void index(Model model, Linker2 linker, Lang lang) {
		L2RIndexerInputHandler indexerHandler = new L2RIndexerInputHandler();
		indexerHandler.setLinker(linker);

		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, model, lang);

		String serializedModel = out.toString();
		InputStream in;
		try {
			in = IOUtils.toInputStream(serializedModel, "UTF-8");
			RDFDataMgr.parse(indexerHandler, in, lang);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void index(InputStream in, Linker2 linker, Lang lang) {
		L2RIndexerInputHandler indexerHandler = new L2RIndexerInputHandler();
		indexerHandler.setLinker(linker);
		RDFDataMgr.parse(indexerHandler, in, lang);
	}

}
