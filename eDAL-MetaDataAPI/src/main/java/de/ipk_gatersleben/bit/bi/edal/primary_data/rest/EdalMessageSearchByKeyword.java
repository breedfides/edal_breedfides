package de.ipk_gatersleben.bit.bi.edal.primary_data.rest;
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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.TextDataBase;

import javax.ws.rs.Produces;

@Path("keywordsearch")
public class EdalMessageSearchByKeyword {

	@GET
	@Path("/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> keywordSearch(@PathParam("keyword") String keyword) {
		List<PrimaryDataEntity> results = DataManager.searchByKeyword(keyword, false, "singleData");
		ArrayList<String> ids = new ArrayList<>();
		for(PrimaryDataEntity entity : results) {
			ids.add(entity.getID());
		}
		return ids;
	}
	@GET
	@Path("/{keyword}/fuzzy")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> fuzzyKeywordSearch(@PathParam("keyword") String keyword) {
		List<PrimaryDataEntity> results = DataManager.searchByKeyword(keyword, true, "singleData");
		ArrayList<String> ids = new ArrayList<>();
		for(PrimaryDataEntity entity : results) {
			ids.add(entity.getID());
		}
		return ids;
	}

}