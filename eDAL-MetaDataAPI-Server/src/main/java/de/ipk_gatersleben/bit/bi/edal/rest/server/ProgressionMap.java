package de.ipk_gatersleben.bit.bi.edal.rest.server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ProgressionMap {
	
	private static ProgressionMap instance;
	private ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>();
	private ProgressionMap() {}
	
	public static ProgressionMap getInstance() {
        if(instance == null){
            instance = new ProgressionMap();
        }
        return instance;
	}

	public ConcurrentHashMap<String, Integer> getMap(){
		return map;
	}
}
