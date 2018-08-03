package br.ufsc.inf.lapesd.l2r;

import java.util.Set;

public interface Index {

	void addToIndex(String key, String value);

	Set<String> load(String key);

}
