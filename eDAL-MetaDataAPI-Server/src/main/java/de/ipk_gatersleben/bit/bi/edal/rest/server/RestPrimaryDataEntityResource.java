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
