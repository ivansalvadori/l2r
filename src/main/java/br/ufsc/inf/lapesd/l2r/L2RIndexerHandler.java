package br.ufsc.inf.lapesd.l2r;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;

public class L2RIndexerHandler implements StreamRDF {

	private Linker linker;

	@Override
	public void start() {

	}

	@Override
	public void triple(Triple triple) {
		linker.addToIndex(triple);
	}

	@Override
	public void quad(Quad quad) {
		// TODO Auto-generated method stub

	}

	@Override
	public void base(String base) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prefix(String prefix, String iri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public void setLinker(Linker linker) {
		this.linker = linker;
	}

}
