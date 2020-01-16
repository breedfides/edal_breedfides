/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package de.ipk_gatersleben.bit.bi.edal.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/entity")
public class RestPrimaryDataEntityResource {

	// This method is called if XML is requested
	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public RestPrimaryDataEntity getXML() {
		RestPrimaryDataEntity entity = new RestPrimaryDataEntity();
		entity.setSummary("RestPrimaryDataEntity XML Summary");
		entity.setDescription("RestPrimaryDataEntity XML Description");
		return entity;
	}

	// This method is called if JSON is requested
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public RestPrimaryDataEntity getJSON() {
		RestPrimaryDataEntity entity = new RestPrimaryDataEntity();
		entity.setSummary("RestPrimaryDataEntity JSON Summary");
		entity.setDescription("RestPrimaryDataEntity JSON Summary");
		return entity;
	}

	// This can be used to test the integration with the browser
	@GET
	@Produces({ MediaType.TEXT_XML })
	public RestPrimaryDataEntity getHTML() {
		RestPrimaryDataEntity entity = new RestPrimaryDataEntity();
		entity.setSummary("RestPrimaryDataEntity Summary");
		entity.setDescription("RestPrimaryDataEntity Description");
		return entity;
	}

}
