/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Locale;

/**
 * data type to describe the {@link EnumDublinCoreElements#LANGUAGE} element
 * 
 * @author arendd
 */
public class EdalLanguage extends UntypedData {

	private static final long serialVersionUID = 4218181705491500341L;

	private Locale language;

	public EdalLanguage(Locale language) {
		this.language = language;
	}

	/**
	 * @return the language
	 */
	public Locale getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(Locale language) {
		this.language = language;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof EdalLanguage) {

			EdalLanguage edalLanguage = (EdalLanguage) datatype;

			if (language.toLanguageTag().compareTo(
					edalLanguage.language.toLanguageTag()) == 0) {
				return super.compareTo(edalLanguage);
			} else {
				return language.toLanguageTag().compareTo(
						edalLanguage.language.toLanguageTag());
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	@Override
	public String toString() {
		return this.getLanguage().toString();
	}

}