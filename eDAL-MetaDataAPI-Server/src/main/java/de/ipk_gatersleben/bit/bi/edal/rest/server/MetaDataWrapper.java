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
package de.ipk_gatersleben.bit.bi.edal.rest.server;

import org.apache.commons.lang3.LocaleUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumCCLicense;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;

/**
 * Class to extract and encapsulate metadata of a JSON Object
 * @author ralfs
 *
 */
public class MetaDataWrapper {
	private final String CREATORS = "creators";
	private final String CONTRIBUTORS = "contributors";
	private final String TITLE = "title";
	private final String DESCRIPTION = "description";
	private final String LICENSE = "license";
	private final String LANGUAGE = "language";
	private final String SUBJECTS = "subjects";
	
	
	private final String FIRSTNAME = "Firstname";
	private final String LASTNAME = "Lastname";
	private final String LEGALNAME = "Legalname";
	private final String ADDRESS = "Address";
	private final String ZIP = "Zip";
	private final String COUNTRY = "Country";
	private final String ORCID = "ORCID";
	
	private UntypedData title;
	private UntypedData description;
	private UntypedData license;
	private EdalLanguage language;
	private Subjects subjects;
	private Persons creators;
	private Persons contributors;
	private LegalPerson legalPerson;
	
	public MetaDataWrapper(JSONObject metadataObject) throws ORCIDException {
		this.title = new UntypedData((String) metadataObject.get(TITLE));
		this.description = new UntypedData((String) metadataObject.get(DESCRIPTION));
		this.license = new UntypedData(EnumCCLicense.valueOf((String) metadataObject.get(LICENSE)).name());
		this.language = new EdalLanguage(LocaleUtils.toLocale((String) metadataObject.get(LANGUAGE)));
		subjects = new Subjects();
		JSONArray subjectsArr = (JSONArray) metadataObject.get(SUBJECTS);
		for(Object subjectStr : subjectsArr) {
			subjects.add(new UntypedData((String) subjectStr));
		}
		this.creators = new Persons();
		this.contributors = new Persons();
		this.legalPerson = new LegalPerson("null","null","null","null");
		fillPersonCollections(creators, (JSONArray) metadataObject.get(CREATORS));
		fillPersonCollections(contributors, (JSONArray) metadataObject.get(CONTRIBUTORS));
	}
	
	private void fillPersonCollections(Persons persons, JSONArray personArray) throws ORCIDException {
		for(Object personObj : personArray) {
			JSONObject person = (JSONObject) personObj;
			if((String) person.get("Legalname") != null) {				
				this.legalPerson = new LegalPerson((String) person.get(LEGALNAME),  (String) person.get(ADDRESS), (String) person.get(ZIP), (String) person.get(COUNTRY));
				persons.add(new LegalPerson((String) person.get(LEGALNAME),  (String) person.get(ADDRESS), (String) person.get(ZIP), (String) person.get(COUNTRY)));
			}else {
				NaturalPerson parsedPerson = new NaturalPerson((String) person.get(FIRSTNAME), (String) person.get(LASTNAME), (String) person.get(ADDRESS),
							(String) person.get(ZIP), (String) person.get(COUNTRY));
				String orcid = (String)person.get(ORCID);
				if(orcid != null && !orcid.isEmpty()) {
					parsedPerson.setOrcid(new ORCID((String) person.get(ORCID)));
				}
				persons.add(parsedPerson);
			}
		}
	}

	public UntypedData getTitle() {
		return title;
	}

	public void setTitle(UntypedData title) {
		this.title = title;
	}

	public UntypedData getDescription() {
		return description;
	}

	public void setDescription(UntypedData description) {
		this.description = description;
	}

	public UntypedData getLicense() {
		return license;
	}

	public void setLicense(UntypedData license) {
		this.license = license;
	}

	public EdalLanguage getLanguage() {
		return language;
	}

	public void setLanguage(EdalLanguage language) {
		this.language = language;
	}

	public Subjects getSubjects() {
		return subjects;
	}

	public void setSubjects(Subjects subjects) {
		this.subjects = subjects;
	}

	public Persons getCreators() {
		return creators;
	}

	public void setCreators(Persons creators) {
		this.creators = creators;
	}

	public Persons getContributors() {
		return contributors;
	}

	public void setContributors(Persons contributors) {
		this.contributors = contributors;
	}

	public LegalPerson getLegalpersons() {
		return this.legalPerson;
	}

	public void setLegalpersons(LegalPerson legalperson) {
		this.legalPerson = legalperson;
	}
}
