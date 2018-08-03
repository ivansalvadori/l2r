package br.ufsc.inf.lapesd.l2r;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryIndex implements Index {

	private Map<String, Set<String>> index = new HashMap<>();

	@Override
	public void addToIndex(String key, String value) {
		Set<String> set = this.index.get(key);
		if (set == null) {
			set = new HashSet<>();
			this.index.put(key, set);
		}
		set.add(value);
	}

	@Override
	public Set<String> load(String key) {
		return this.index.get(key);
	}

}
