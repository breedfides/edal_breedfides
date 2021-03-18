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
package de.ipk_gatersleben.bit.bi.edal.primary_data.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.TextDataBase;

import javax.ws.rs.Produces;

@Path("message")
public class EdalMessageRest {

	@GET
	@Path("/{msg}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> getMessage(@PathParam("msg") String msg) {
		TextDataBase db  = new TextDataBase();
		db.append(msg);
		return db.getData();
	}

}
