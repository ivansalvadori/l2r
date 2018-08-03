package br.ufsc.inf.lapesd.l2r;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerCompressionWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PersistentIndex implements Index {

	private String mapName;
	private HTreeMap<String, String> map = null;
	private DB db = null;

	public PersistentIndex(String mapName) {
		super();
		this.mapName = mapName;
		boolean mapAlreadyExists = false;
		db = DBMaker.fileDB("index.db").fileMmapEnable().make();
		Iterable<String> allNames = db.getAllNames();
		for (String existingMap : allNames) {
			if (existingMap.equals(mapName)) {
				mapAlreadyExists = true;
			}
		}
		if (!mapAlreadyExists) {
			map = db.hashMap(mapName).keySerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).valueSerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).create();
		}
		db.close();
	}

	@Override
	public void addToIndex(String key, String value) {
		db = DBMaker.fileDB("index.db").fileMmapEnable().make();
		map = db.hashMap(mapName).keySerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).valueSerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).open();

		if (!map.containsKey(key)) {
			Set<String> valueAsSet = new HashSet<>();
			valueAsSet.add(value);
			String jsonNewValue = new Gson().toJson(valueAsSet);
			map.put(key, jsonNewValue);
		} else {
			String storedValue = map.get(key);
			Type collectionType = new TypeToken<Set<String>>() {
			}.getType();
			Set<String> setOfStoredvalues = new Gson().fromJson(storedValue, collectionType);
			setOfStoredvalues.add(value);
			String jsonNewValue = new Gson().toJson(setOfStoredvalues);
			map.replace(key, jsonNewValue);

		}

		db.close();
	}

	@Override
	public Set<String> load(String key) {
		if (!new File("index.db").exists()) {
			return null;
		}

		DB db = DBMaker.fileDB("index.db").fileMmapEnable().readOnly().make();
		HTreeMap<String, String> map = db.hashMap(mapName).keySerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).valueSerializer(new SerializerCompressionWrapper<String>(Serializer.STRING)).open();
		String storedValue = map.get(key);

		Set<String> setOfStoredvalues = new Gson().fromJson(storedValue, new TypeToken<Set<String>>() {
		}.getType());

		db.close();

		return setOfStoredvalues;
	}

}
