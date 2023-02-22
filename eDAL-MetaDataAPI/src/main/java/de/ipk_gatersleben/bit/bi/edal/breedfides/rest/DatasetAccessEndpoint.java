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

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.JwtValidator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.SearchProviderImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
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
	public Response getMetadata(@Context HttpServletRequest request, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize, @QueryParam("subjects") String subjects,
			@QueryParam("licenses") String licenses, @QueryParam("languages") String languages) {

		if (request.getHeader("Authorization") != null) {

			// cut of "Bearer"
			String jwt = request.getHeader("Authorization").substring(7);

			InfoEndpoint.getLogger().info(
					"Got JWT '" + jwt.substring(0, 3) + "..." + jwt.substring(jwt.length() - 3, jwt.length()) + "'");

			String serialNumber = null;

			try {
//				JwtValidator.validate(jwt);
				serialNumber = JwtValidator.getSerialNumber(jwt);
				InfoEndpoint.getLogger().info("SerialNumber of JWT: " + serialNumber);

			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Given JWT is invalid: " + e.getMessage())
						.build();
			}
			try {
				FileSystemImplementationProvider filesystemImplementationProvider = ((FileSystemImplementationProvider) DataManager
						.getImplProv());

				PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(filesystemImplementationProvider,
						EdalHelpers.authenticateUserWithJWT("jwt"));
				
				
				
				MetaData metadata = filesystemImplementationProvider.createMetaDataInstance();
				
				Subjects subjectsMetadata = new Subjects();
				
				subjectsMetadata.add(new UntypedData(subjects));
				
				metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjectsMetadata);
				
				List<PrimaryDataEntity> list = rootDirectory.searchByMetaData(metadata, false, false);
				
				System.out.println(list);
				
				
				
				
				
				try {
					SearchProviderImplementation si = (SearchProviderImplementation) filesystemImplementationProvider.getSearchProvider().getDeclaredConstructor().newInstance();
					
					HashSet<Integer> set = 	si.searchByKeyword("hordeum vulgare", false,"d" );
					
					System.out.println(set);
					
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {

					e.printStackTrace();
				}
				
//				MetaData
				
//				rootDirectory.searchByMetaData(null, false, true);
				
				
			} catch (PrimaryDataDirectoryException | EdalAuthenticateException | MetaDataException e) {
				e.printStackTrace();
			}

			return null;
		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Please check your Authorizaton Header")
					.build();

		}
	}

}
