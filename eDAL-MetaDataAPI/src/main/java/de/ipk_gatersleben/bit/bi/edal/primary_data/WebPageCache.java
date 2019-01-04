/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

	private Cache<String, ByteArrayOutputStream> cache;
	private CacheManager cacheManager;
	public String cacheName;

	public WebPageCache(String cacheName) {
		this.cacheName = cacheName;
	}

	public void init() {
		this.cacheManager = ((FileSystemImplementationProvider) DataManager.getImplProv()).getCacheManager();
		this.cache = this.cacheManager.getCache(this.cacheName, String.class, ByteArrayOutputStream.class);
	}

	public void put(String key, ByteArrayOutputStream value) {
		if (key != null) {
			this.cache.put(key, value);
		}
	}

	public ByteArrayOutputStream get(String key) {
		if (this.cache.get(key) == null) {
			return null;
		} else {
			return this.cache.get(key);
		}
	}

	public void clean() {
		while (this.cache.iterator().hasNext()) {
			Entry<String, ByteArrayOutputStream> entry = this.cache.iterator().next();
			this.cache.remove(entry.getKey());
		}
		this.cache.clear();
	}

}