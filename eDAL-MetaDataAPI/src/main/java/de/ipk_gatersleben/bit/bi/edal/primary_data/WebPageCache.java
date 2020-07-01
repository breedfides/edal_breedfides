/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
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