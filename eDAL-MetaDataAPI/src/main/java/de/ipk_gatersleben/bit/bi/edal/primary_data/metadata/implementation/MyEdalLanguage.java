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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.Locale;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.FieldBridge;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

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
	//@Field(store = Store.YES, bridge = @FieldBridge(impl = LanguageBridge.class))
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES, valueBridge = @ValueBridgeRef(type = LanguageBridge.class)
    )
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
