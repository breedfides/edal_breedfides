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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.JwtValidator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.SearchProviderBreedFidesImplementation;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * REST endpoints for relevant function to get data from the BreedFides portal
 * 
 * @author arendd
 *
 */
@Path("access")
public class DatasetAccessEndpoint {

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {

		InfoEndpoint.getLogger().info("Call 'access/info/' endpoint");

		return "Call 'access/info/' endpoint";
	}

	@GET
	@Path("/datasets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMetadata(@QueryParam("datasetRoot") String datasetRoot, @Context HttpServletRequest request,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize, @QueryParam("subjects") String subjects,
			@QueryParam("licenses") String licenses, @QueryParam("languages") String languages,
			@QueryParam("keywords") String keywords) {

		InfoEndpoint.getLogger().info("Call 'access/datasets/' endpoint");

		if (page == 0) {
			page = 1;
		}

		if (pageSize == 0) {
			pageSize = 25;
		}

		if (request.getHeader("Authorization") != null) {

			// cut of "Bearer"
			String jwt = request.getHeader("Authorization").substring(7);

			try {
				JwtValidator.validate(jwt);
				String serialNumber = JwtValidator.getSerialNumber(jwt);
				InfoEndpoint.getLogger()
						.info("Got JWT '" + jwt.substring(0, 3) + "..." + jwt.substring(jwt.length() - 3, jwt.length())
								+ "' with valid Serialnumber : " + serialNumber);
			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Given JWT is invalid: " + e.getMessage())
						.build();
			}

		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Please check your Authorizaton Header")
					.build();
		}

		try {
			if (keywords == null || keywords.isBlank() || keywords.isEmpty()) {

				SearchProviderBreedFidesImplementation searchProvider = (SearchProviderBreedFidesImplementation) DataManager
						.getImplProv().getSearchProviderBreedFides().getDeclaredConstructor().newInstance();

				String resultJSON = searchProvider.getAllUserDatasets(page, pageSize);

				ObjectNode jsonNode = (ObjectNode) new ObjectMapper().readTree(resultJSON);

				InfoEndpoint.getLogger().info("Get all " + jsonNode.findValue("results").size() + " User Datasets ");

				return Response.status(Status.OK).entity(resultJSON).build();

			}

			else {

				PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(DataManager.getImplProv(),
						EdalHelpers.authenticateUserWithJWT("jwt"));

				SearchProviderBreedFidesImplementation searchProvider = (SearchProviderBreedFidesImplementation) DataManager
						.getImplProv().getSearchProviderBreedFides().getDeclaredConstructor().newInstance();

				String resultJSON = searchProvider.breedFidesKeywordSearch(rootDirectory, keywords, true, false, page,
						pageSize);

				ObjectNode jsonNode = (ObjectNode) new ObjectMapper().readTree(resultJSON);

				InfoEndpoint.getLogger().info("Found " + jsonNode.findValue("results").size() + " results");

				return Response.status(Status.OK).entity(resultJSON).build();

			}
		} catch (Exception e) {
			Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
		}

		return null;

	}

}
