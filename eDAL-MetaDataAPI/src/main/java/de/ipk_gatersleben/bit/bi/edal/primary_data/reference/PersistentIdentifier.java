/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

/**
 * {@link Enum} to provide the implementation classes for the
 * {@link Referenceable} interface of the different identifier systems.
 * 
 * @author arendd
 */
public enum PersistentIdentifier {

	/**
	 * DOI interface
	 */
	DOI,
	/**
	 * URN interface
	 */
	URN,
	/**
	 * URL interface
	 */
	URL;

	public Class<? extends Referenceable> getImplClass() {
		switch (this) {
		case DOI:
			return DataCiteReference.class;
		case URN:
			return URNReference.class;
		case URL:
			return URLReference.class;
		}
		return null;
	}
}