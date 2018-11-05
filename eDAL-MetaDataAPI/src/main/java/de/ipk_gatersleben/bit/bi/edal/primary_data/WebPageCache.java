/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.ByteArrayOutputStream;

import org.ehcache.Cache;
import org.ehcache.Cache.Entry;
import org.ehcache.CacheManager;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;

/**
 * A class for caching webpages.
 * 
 * @author arendd
 */

public class WebPageCache {
	
	private static Cache<String, ByteArrayOutputStream> cache;
	private static CacheManager cacheManager;
	public String cacheName;

	public WebPageCache(String cacheName) {
		this.cacheName = cacheName;
	}

	public void init() {
		cacheManager = ((FileSystemImplementationProvider) DataManager.getImplProv()).getCacheManager();
		cache = cacheManager.getCache(this.cacheName, String.class, ByteArrayOutputStream.class);
	}

	public void put(String key, ByteArrayOutputStream value) {
		if (key != null) {
			cache.put(key, value);
		}
	}

	public ByteArrayOutputStream get(String key) {
		if (cache.get(key) == null) {
			return null;
		} else {
			return cache.get(key);
		}
	}

	public void clean() {
		while (cache.iterator().hasNext()) {
			Entry<String, ByteArrayOutputStream> entry= cache.iterator().next();
			cache.remove(entry.getKey());	
		}
		cache.clear();
	}

}