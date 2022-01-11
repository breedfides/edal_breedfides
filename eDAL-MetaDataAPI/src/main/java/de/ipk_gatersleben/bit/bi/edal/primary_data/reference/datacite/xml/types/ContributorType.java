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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types;

import java.util.HashMap;
import java.util.Map;

/**
 * The type of contributor of the resource.
 * 
 * @author arendd
 */
public enum ContributorType {

	/**
	 * Constant CONTACTPERSON
	 */
	ContactPerson("ContactPerson"),
	/**
	 * Constant DATACOLLECTOR
	 */
	DataCollector("DataCollector"),
	/**
	 * Constant DATAMANAGER
	 */
	DataManager("DataManager"),
	/**
	 * Constant DISTRIBUTOR
	 */
	Distributor("Distributor"),
	/**
	 * Constant EDITOR
	 */
	Editor("Editor"),
	/**
	 * Constant FUNDER
	 */
	Funder("Funder"),
	/**
	 * Constant HOSTINGINSTITUTION
	 */
	HostingInstitution("HostingInstitution"),
	/**
	 * Constant OTHER
	 */
	Other("Other"),
	/**
	 * Constant PRODUCER
	 */
	Producer("Producer"),
	/**
	 * Constant PROJECTLEADER
	 */
	ProjectLeader("ProjectLeader"),
	/**
	 * Constant PROJECTMEMBER
	 */
	ProjectMember("ProjectMember"),
	/**
	 * Constant REGISTRATIONAGENCY
	 */
	RegistrationAgency("RegistrationAgency"),
	/**
	 * Constant REGISTRATIONAUTHORITY
	 */
	RegistrationAuthority("RegistrationAuthority"),
	/**
	 * Constant RELATEDPERSON
	 */
	RelatedPerson("RelatedPerson"),
	/**
	 * Constant RIGHTSHOLDER
	 */
	RightsHolder("RightsHolder"),
	/**
	 * Constant RESEARCHER
	 */
	Researcher("Researcher"),
	/**
	 * Constant RESEARCHGROUP
	 */
	ResearchGroup("ResearchGroup"),
	/**
	 * Constant SPONSOR
	 */
	Sponsor("Sponsor"),
	/**
	 * Constant SUPERVISOR
	 */
	Supervisor("Supervisor"),
	/**
	 * Constant WORKPACKAGELEADER
	 */
	WorkPackageLeader("WorkPackageLeader");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, ContributorType> ENUM_CONSTANTS = new HashMap<String, ContributorType>();

	static {
		for (ContributorType c : ContributorType.values()) {
			ContributorType.ENUM_CONSTANTS.put(c.value, c);
		}

	};

	private ContributorType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the contributor type
	 * @return the constant for this value
	 */
	public static ContributorType fromValue(final String value) {
		ContributorType c = ContributorType.ENUM_CONSTANTS.get(value);
		if (c != null) {
			return c;
		}
		throw new IllegalArgumentException(value);
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
	}

	/**
	 * Method toString.
	 * 
	 * @return the value of this constant
	 */
	public String toString() {
		return this.value;
	}

	/**
	 * Method value.
	 * 
	 * @return the value of this constant
	 */
	public String value() {
		return this.value;
	}

}
