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