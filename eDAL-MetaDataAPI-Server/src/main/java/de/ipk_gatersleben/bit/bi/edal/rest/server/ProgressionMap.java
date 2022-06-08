package de.ipk_gatersleben.bit.bi.edal.rest.server;

import java.util.HashMap;

public class ProgressionMap {
	
	private static ProgressionMap instance;
	private HashMap<String,Integer> map = new HashMap<>();
	private ProgressionMap() {}
	
	public static ProgressionMap getInstance() {
        if(instance == null){
            instance = new ProgressionMap();
        }
        return instance;
	}

	public HashMap<String, Integer> getMap(){
		return map;
	}
}
