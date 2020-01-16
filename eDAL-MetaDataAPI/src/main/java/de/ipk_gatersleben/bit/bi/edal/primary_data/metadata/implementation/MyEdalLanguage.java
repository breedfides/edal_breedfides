/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.Locale;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link EdalLanguage} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("15")
@Indexed
public class MyEdalLanguage extends MyUntypedData {

	private static final long serialVersionUID = -7524581040275981979L;

	private Locale language;

	/**
	 * Default constructor for {@link MyEdalLanguage} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyEdalLanguage() {
	}

	/**
	 * Copy constructor to convert public {@link EdalLanguage} to private
	 * {@link MyEdalLanguage}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyEdalLanguage(final UntypedData edal) {

		super(edal);

		if (edal instanceof EdalLanguage) {

			final EdalLanguage language = (EdalLanguage) edal;

			this.setLanguage(language.getLanguage());

		}
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

	@Override
	public String toString() {
		return "EdalLanguage [language=" + language + "]";
	}

}
