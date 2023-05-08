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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;

import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.JwtValidator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ZipThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;

/**
 * REST endpoints for relevant function to download data to the BreedFides
 * portal
 * 
 * @author arendd
 *
 */
@Path("download")
public class DatasetDownloadEndpoint {

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {

		InfoEndpoint.getLogger().info("Call 'download/info/' endpoint");
		return "Call 'download/info/' endpoint";
	}

	@GET
	@Path("/dataset")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadDataset(@QueryParam("datasetOwner") String datasetOwner,
			@Context HttpServletRequest request, @QueryParam("datasetRoot") String datasetRoot) {

		InfoEndpoint.getLogger().info("Call 'download/datasets/' endpoint");

		if (request.getHeader("Authorization") != null) {

			// cut of "Bearer"
			String jwt = request.getHeader("Authorization").substring(7);

			String serialNumber = null;

			try {
				JwtValidator.validate(jwt);
				serialNumber = JwtValidator.getSerialNumber(jwt);
				InfoEndpoint.getLogger()
						.info("Got JWT '" + jwt.substring(0, 3) + "..." + jwt.substring(jwt.length() - 3, jwt.length())
								+ "' with valid Serialnumber : " + serialNumber);

			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Given JWT is invalid: " + e.getMessage())
						.build();
			}

			String zipfileName = "";
			try {

				zipfileName = ((PrimaryDataDirectory) EdalFunctions.getRootDirectory()
						.getPrimaryDataEntity(datasetOwner)).getPrimaryDataEntity(datasetRoot).getName() + ".zip";
			} catch (PrimaryDataDirectoryException e) {
				e.printStackTrace();
				InfoEndpoint.getLogger().error(e.getMessage());
			}

			StreamingOutput fileStream = new StreamingOutput() {

				@Override
				public void write(OutputStream output) {

					try (ZipOutputStream zipStream = new ZipOutputStream(output)) {

						PrimaryDataDirectory datasetDirectory = (PrimaryDataDirectory) ((PrimaryDataDirectory) EdalFunctions
								.getRootDirectory().getPrimaryDataEntity(datasetOwner))
								.getPrimaryDataEntity(datasetRoot);

						readPrimaryDataDirectoryIntoZipOutputStream(zipStream, datasetDirectory, true);

					} catch (Exception e) {
						e.printStackTrace();
						InfoEndpoint.getLogger().error(e.getMessage());
					}

				}
			};

			return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition", "attachment; filename=" + zipfileName).build();
		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Please check your Authorizaton Header")
					.build();

		}

	}

	private void readPrimaryDataDirectoryIntoZipOutputStream(ZipOutputStream zipOutputStream,
			PrimaryDataDirectory entity, boolean log)
			throws PrimaryDataDirectoryException, PrimaryDataFileException, MetaDataException, IOException {

		if (log) {
			DataManager.getImplProv().getLogger().info("Preparing Zip File for '" + entity.getName() + "'");
		}

		List<PrimaryDataEntity> list = entity.listPrimaryDataEntities();

		DataManager.getImplProv().getLogger().debug("Adding directory " + entity.getPath());

		for (PrimaryDataEntity primaryDataEntity : list) {

			// if the file is directory, call the function recursively
			if (primaryDataEntity.isDirectory()) {
				readPrimaryDataDirectoryIntoZipOutputStream(zipOutputStream, (PrimaryDataDirectory) primaryDataEntity,
						false);
				continue;
			}

			DataManager.getImplProv().getLogger().debug("Adding file " + primaryDataEntity.getPath());

			/** remove rootDirectory and userDirectory from path **/
			ZipEntry zipEntry = new ZipEntry(
					primaryDataEntity.getPath().substring(primaryDataEntity.getPath().indexOf("/", 2) + 1));

			DateEvents dateEvents = primaryDataEntity.getMetaData().getElementValue(EnumDublinCoreElements.DATE);

			Set<EdalDate> dateEventSets = dateEvents.getSet();

			for (EdalDate edalDate : dateEventSets) {
				if (edalDate.getEvent().equals(EdalDate.STANDART_EVENT_TYPES.CREATED.toString())) {
					zipEntry.setTime(edalDate.getStartDate().getTimeInMillis());
					break;
				}
			}

			String mimetype = ((DataFormat) primaryDataEntity.getCurrentVersion().getMetaData()
					.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType();

			String mimeTypeGroup = mimetype.substring(0, mimetype.indexOf("/")).toUpperCase();

			zipOutputStream.setMethod(ZipEntry.DEFLATED);

			if (ZipThread.ENUM_LIST.contains(mimeTypeGroup)) {
				zipOutputStream.setLevel(1);
			} else {
				zipOutputStream.setLevel(0);
			}

			zipOutputStream.putNextEntry(zipEntry);

			((PrimaryDataFile) primaryDataEntity).read(zipOutputStream);

			zipOutputStream.flush();
			zipOutputStream.closeEntry();

		}
		if (log) {
			DataManager.getImplProv().getLogger().info("ZipOutputStream for '" + entity.getName() + "' finished");
		}

	}

}
