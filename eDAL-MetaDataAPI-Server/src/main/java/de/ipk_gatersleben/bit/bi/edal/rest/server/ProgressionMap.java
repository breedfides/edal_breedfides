/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rest.server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Singleton ConcurrentHashmap currently used in the DataSubmissionService.class
 * to allow multiple Threads to obtain the progress of a file upload
 * @author ralfs
 *
 */
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
