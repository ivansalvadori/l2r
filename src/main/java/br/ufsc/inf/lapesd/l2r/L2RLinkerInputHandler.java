package br.ufsc.inf.lapesd.l2r;

import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;

public class L2RLinkerInputHandler implements StreamRDF {

	private StreamRDF outputHandler;

	private Linker2 linker;

	@Override
	public void start() {

	}

	@Override
	public void triple(Triple triple) {
		Set<Triple> linkedTriples = linker.link(triple);
		for (Triple linkedTriple : linkedTriples) {
			outputHandler.triple(linkedTriple);
		}
	}

	@Override
	public void quad(Quad quad) {
		// TODO Auto-generated method stub

	}

	@Override
	public void base(String base) {
		outputHandler.base(base);

	}

	@Override
	public void prefix(String prefix, String iri) {
		outputHandler.prefix(prefix, iri);

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public void setLinker(Linker2 linker) {
		this.linker = linker;
	}

	public void setOutputHandler(StreamRDF outputHandler) {
		this.outputHandler = outputHandler;
	}

}
