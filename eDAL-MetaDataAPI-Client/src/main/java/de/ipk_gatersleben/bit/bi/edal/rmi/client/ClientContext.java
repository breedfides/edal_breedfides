/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client;

/**
 * Class to provide current client context to server.
 * 
 * @author arendd
 */
public abstract class ClientContext {

	protected ClientDataManager clientDataManager;

	/**
	 * Default constructor set current {@link ClientDataManager}.
	 * 
	 * @param client
	 *            the current {@link ClientDataManager} for this client context.
	 */
	protected ClientContext(ClientDataManager client) {
		this.clientDataManager = client;

	}

}
