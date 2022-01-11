package de.ipk_gatersleben.bit.bi.edal.primary_data;
/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ManagedAsync;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.helper.Search;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;

import javax.ws.rs.Produces;

@Path("keywordsearch")
public class EdalMessageSearchByKeyword {

	@GET
	@Path("/{keyword}")
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray keywordSearch(@PathParam("keyword") String keyword) {
		HashSet<Integer> ids = Search.searchByKeyword(keyword, false, PublicVersionIndexWriterThread.PUBLICREFERENCE);
		JSONArray finalArray = new JSONArray();
		Session session = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSessionFactory().openSession();
		for(Integer id : ids) {
			PublicReferenceImplementation reference = session.get(PublicReferenceImplementation.class, id);
			JSONObject obj = new JSONObject();
			obj.put("year", reference.getAcceptedDate().get(Calendar.YEAR));
			try {
				obj.put("doi", reference.getAssignedID());
			} catch (PublicReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			obj.put("title", reference.getVersion().getMetaData().toString());
			String internalID = reference.getInternalID();
			obj.put("downloads", String.valueOf(VeloCityHtmlGenerator.downloadedVolume.get(internalID)));
			obj.put("accesses", String.valueOf(VeloCityHtmlGenerator.uniqueAccessNumbers.get(internalID)));	
			if(VeloCityHtmlGenerator.ipMap.get(internalID) != null) {
				obj.put("locations",
						GenerateLocations.generateGpsLocationsToJson(VeloCityHtmlGenerator.ipMap.get(internalID)));
			}
			finalArray.add(obj);
		}
		return finalArray;
	}
	@GET
	@ManagedAsync
	@Path("/{keyword}/fuzzy")
	@Produces(MediaType.APPLICATION_JSON)
	public HashSet<Integer> fuzzyKeywordSearch(@PathParam("keyword") String keyword) {
		return Search.searchByKeyword(keyword, true, PublicVersionIndexWriterThread.PUBLICREFERENCE);
	}

}