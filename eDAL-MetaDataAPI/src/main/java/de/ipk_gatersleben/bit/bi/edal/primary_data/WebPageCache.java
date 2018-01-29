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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * A class for caching webpages.
 * 
 * @author arendd
 */

public class WebPageCache {
	private static Cache cache = null;
	private static CacheManager manager = null;
	public static int cache_depth = 0;

	public WebPageCache() {
	}

	public static void release() {
		if (manager != null) {
			manager.shutdown();
		}
	}

	public void init() {
		manager = CacheManager.create(WebPageCache.class.getResourceAsStream("webcache.xml"));

		cache = manager.getCache("jetty.webpage.cache");
	}

	public void put(String key, ByteArrayOutputStream fileSystemHandler) {
		if (key != null) {
			cache.put(new Element(key, fileSystemHandler));
		}
	}

	public ByteArrayOutputStream get(String key) {
		if (cache.isKeyInCache(key)) {
			if ((cache.get(key)) == null) {
				return null;
			}
			return (ByteArrayOutputStream) cache.get(key).getObjectValue();
		} else {
			return null;
		}
	}

	public void clean() {
		cache.removeAll();
	}

}
