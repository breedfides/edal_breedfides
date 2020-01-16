/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
