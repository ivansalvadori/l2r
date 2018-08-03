package br.ufsc.inf.lapesd.l2r.test;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import br.ufsc.inf.lapesd.l2r.Linker;

public class LinkerNoContextPersistedIndexTest extends LinkerNoContextTest {

	Linker linker = new Linker();

	@Before
	public void before() {
		this.linker = new Linker();
		new File("index.db").delete();
		this.linker.persistentIndex();
	}

	@After
	public void after() {
		new File("index.db").delete();
	}

}
