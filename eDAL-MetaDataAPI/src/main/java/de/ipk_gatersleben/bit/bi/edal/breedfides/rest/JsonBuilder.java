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
package de.ipk_gatersleben.bit.bi.edal.breedfides.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonBuilder {

	private ObjectMapper mapper;
	private ObjectNode rootNode;

	public JsonBuilder() {
		this.mapper = new ObjectMapper();
		this.rootNode = mapper.createObjectNode();
	}

	public final void put(String key, String value) {

		this.rootNode.put(key, value);
	}

	public String getJsonString() {
		try {
			return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.rootNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}

	}
}
