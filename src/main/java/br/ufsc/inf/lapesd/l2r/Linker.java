package br.ufsc.inf.lapesd.l2r;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDFS;

public class Linker {

	private Contextualizer contextualizer;
	private Index index = new InMemoryIndex();
	private boolean contextEnabled;
	private boolean persistentIndex = false;

	public void inMemoryIndex() {
		this.index = new InMemoryIndex();
	}

	public void persistentIndex() {
		this.persistentIndex = true;
		this.index = new PersistentIndex("linker");
		if (contextEnabled) {
			this.contextualizer = new Contextualizer();
			this.contextualizer.persistentIndex();
		}
	}

	public void contextEnabled(boolean contextEnabled) {
		this.contextEnabled = contextEnabled;
		if (contextEnabled) {
			this.contextualizer = new Contextualizer();
			if (persistentIndex) {
				this.contextualizer.persistentIndex();
			}
		}
	}

	public void addToIndex(Triple triple) {
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();
		if (predicate.getURI().equals(RDFS.label.getURI())) {
			this.index.addToIndex(object.getLiteralValue().toString().toLowerCase(), subject.getURI());
		}
		if (this.contextualizer != null) {
			this.contextualizer.addToIndex(triple);
		}
	}

	public Set<Triple> link(Triple triple) {
		Set<Triple> linkedTriples = new HashSet<>();
		Node object = triple.getObject();
		if (object.isLiteral()) {
			if (triple.getPredicate().getURI().equals(RDFS.label.getURI())) {
				linkedTriples.add(triple);
				return linkedTriples;
			}
			Set<String> convertedObject = this.index.load(object.getLiteral().toString().toLowerCase());
			if (convertedObject != null) {
				for (String uri : convertedObject) {
					Node newObject = NodeFactory.createURI(uri);
					Triple linkedTriple = Triple.create(triple.getSubject(), triple.getPredicate(), newObject);

					if (this.contextualizer != null) {
						Set<String> validContext = this.contextualizer.loadContext(linkedTriple.getPredicate().getURI());
						Set<String> types = contextualizer.getType(uri);
						if (types == null) {
							linkedTriples.add(linkedTriple);
							continue;
						}
						for (String type : types) {
							if (validContext == null) {
								linkedTriples.add(linkedTriple);
							} else if (validContext != null && validContext.contains(type)) {
								linkedTriple = Triple.create(triple.getSubject(), triple.getPredicate(), newObject);
								linkedTriples.add(linkedTriple);
								continue;
							}
						}
					} else {
						linkedTriples.add(linkedTriple);
					}
				}
			} else {
				linkedTriples.add(triple);
			}
		} else {
			linkedTriples.add(triple);
		}
		return linkedTriples;
	}
}
