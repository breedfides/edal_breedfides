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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
@javax.ws.rs.Path("access")
public class DatasetAccessEndpoint {

	@GET
	@javax.ws.rs.Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {

		InfoEndpoint.getLogger().info("Call 'access/info/' endpoint");

		return "Call 'access/info/' endpoint";
	}

	@GET
	@javax.ws.rs.Path("/datasets")
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

			InfoEndpoint.getLogger().info(
					"Got JWT '" + jwt.substring(0, 3) + "..." + jwt.substring(jwt.length() - 3, jwt.length()) + "'");

			try {
				JwtValidator.validate(jwt);
				String serialNumber = JwtValidator.getSerialNumber(jwt);
				InfoEndpoint.getLogger().info("SerialNumber of JWT: " + serialNumber);
			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Given JWT is invalid: " + e.getMessage())
						.build();
			}

		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Please check your Authorizaton Header")
					.build();
		}

		if (keywords == null) {
			try {
				FileSystemImplementationProvider filesystemImplementationProvider = ((FileSystemImplementationProvider) DataManager
						.getImplProv());

				SearchProviderBreedFidesImplementation searchProvider = (SearchProviderBreedFidesImplementation) filesystemImplementationProvider
						.getSearchProviderBreedFides().getDeclaredConstructor().newInstance();

				String result = searchProvider.getAllUserDatasets(page, pageSize);

				InfoEndpoint.getLogger().info("Get all User Datasets " + result);

				return Response.status(Status.OK).entity(result).build();
			} catch (Exception e) {

				Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
			}

		} else if (!keywords.isEmpty()) {
			try {
				FileSystemImplementationProvider filesystemImplementationProvider = ((FileSystemImplementationProvider) DataManager
						.getImplProv());

				PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(filesystemImplementationProvider,
						EdalHelpers.authenticateUserWithJWT("jwt"));

				SearchProviderBreedFidesImplementation searchProvider = (SearchProviderBreedFidesImplementation) filesystemImplementationProvider
						.getSearchProviderBreedFides().getDeclaredConstructor().newInstance();

				String result = searchProvider.breedFidesKeywordSearch(rootDirectory, keywords, true, false, page,
						pageSize);

				InfoEndpoint.getLogger().info("Found " + result);
				
				System.out.println(searchProvider.drillDown("+Allfields:triticum +EntityType:dataset"));

				return Response.status(Status.OK).entity(result).build();
			} catch (Exception e) {

				Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
			}
		}

		return null;

	}

}
