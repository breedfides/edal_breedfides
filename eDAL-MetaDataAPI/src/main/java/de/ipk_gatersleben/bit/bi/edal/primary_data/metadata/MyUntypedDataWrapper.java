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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;

public class MyUntypedDataWrapper {
	private StringBuilder givenName = new StringBuilder();
	private StringBuilder sureName= new StringBuilder();
	private StringBuilder country= new StringBuilder();
	private StringBuilder zip= new StringBuilder();
	private StringBuilder addressLine= new StringBuilder();
	private StringBuilder legalName= new StringBuilder();
	private StringBuilder subjects= new StringBuilder();
	private StringBuilder relations= new StringBuilder();
	private StringBuilder checkSum= new StringBuilder();
	private StringBuilder algorithm= new StringBuilder();
	private String identifier;
	private String mimeType;
	private String coverage;
	private String title;
	private String type;
	private String description;
	private String format;
	private String rights;
	private String source;
	private String language;
	private int versionId;
	private Long size;
	private Calendar startDate;
	private Calendar endDate;
	private MetaData metaData;
	
	 public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	public MetaData getMetaData() {
		return metaData;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	
	public void addPerson(Person person) {
		this.addAddressLine(person.getAddressLine());
		this.addZip(person.getZip());
		this.addCountry(person.getCountry());
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getRelations() {
		return relations.toString();
	}
	public void setRelations(StringBuilder relations) {
		this.relations = relations;
	}
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	public void setGivenName(StringBuilder givenName) {
		this.givenName = givenName;
	}
	public void setSureName(StringBuilder sureName) {
		this.sureName = sureName;
	}
	public void setCountry(StringBuilder country) {
		this.country = country;
	}
	public void setZip(StringBuilder zip) {
		this.zip = zip;
	}
	public void setAddressLine(StringBuilder addressLine) {
		this.addressLine = addressLine;
	}
	public void setLegalName(StringBuilder legalName) {
		this.legalName = legalName;
	}
	public void setSubjects(StringBuilder subjects) {
		this.subjects = subjects;
	}
	public void setCheckSum(StringBuilder checkSum) {
		this.checkSum = checkSum;
	}
	public void setAlgorithm(StringBuilder algorithm) {
		this.algorithm = algorithm;
	}
	public void addGivenName(String givenName) {
		if(givenName != null && givenName.length() > 0) {
			this.givenName.append(givenName);
			this.givenName.append(",");
		}
	}
	public void addSureName(String sureName) {
		if(sureName != null && sureName.length() > 0) {
			this.sureName.append(sureName);
			this.sureName.append(",");
		}
	}
	public void addCountry(String country) {
		if(country != null && country.length() > 0) {
			this.country.append(country);
			this.country.append(",");
		}
	}
	public void addZip(String zip) {
		if(zip != null && zip.length() > 0) {
			this.zip.append(zip);
			this.zip.append(",");
		}
	}
	public void addAddressLine(String addressLine) {
		if(addressLine != null && addressLine.length() > 0) {
			this.addressLine.append(addressLine);
			this.addressLine.append(",");
		}
	}
	public void addLegalName(String legalName) {
		if(legalName != null && legalName.length() > 0) {
			this.legalName.append(legalName);
			this.legalName.append(",");
		}
	}
	public void addSubjects(String subjects) {
		if(subjects != null && subjects.length() > 0) {
			this.subjects.append(subjects);
			this.subjects.append(",");
		}
	}
	public void addAlgorithm(String algorithm) {
		if(algorithm != null && algorithm.length() > 0) {
			this.algorithm.append(algorithm);
			this.algorithm.append(",");
		}
	}
	public void addCheckSum(String checkSum) {
		if(checkSum != null && checkSum.length() > 0) {
			this.checkSum.append(checkSum);
			this.checkSum.append(",");
		}
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getAddressLine() {
		return addressLine.toString();
	}
	
	public void addValue(EnumDublinCoreElements key, UntypedData value) {
		if(key.equals(EnumDublinCoreElements.CREATOR)) {
			Set<Person> persons = ((Persons)value).getPersons();
			for(Person person : persons) {
				NaturalPerson naturalPerson = (NaturalPerson) person;
				this.addGivenName(naturalPerson.getGivenName());
				this.addSureName(naturalPerson.getSureName());
				this.addPerson(person);
			}
		}
		else if(key.equals(EnumDublinCoreElements.PUBLISHER)) {
				this.addLegalName(((LegalPerson)value).getLegalName());
				this.addPerson((LegalPerson)value);
			}
		else if(key.equals(EnumDublinCoreElements.SUBJECT) && value != null) {
			Set<UntypedData> subjects =  ((Subjects)value).getSubjects();
			for(UntypedData subject : subjects) {
				this.subjects.append(value.getString());
				this.subjects.append(",");
			}
		}else if(key.equals(EnumDublinCoreElements.RELATION)&& value != null) {
			Collection<Identifier> relations = ((IdentifierRelation)value).getRelations();
			for(Identifier identifier : relations) {
				this.relations.append(((Identifier)value).getID());
				this.relations.append(",");
			}
		}
		else if(key.equals(EnumDublinCoreElements.CHECKSUM) && value != null) {
			for(CheckSumType checkSum : ((CheckSum)value)) {			
				this.addAlgorithm(checkSum.getAlgorithm());
				this.addCheckSum(checkSum.getCheckSum());
			}
		}
		else if(key.equals(EnumDublinCoreElements.IDENTIFIER)) {
			this.identifier = ((Identifier) value).getID();
		}
		else if(key.equals(EnumDublinCoreElements.TITLE)) {
			this.setTitle(value.getString());
		}else if(key.equals(EnumDublinCoreElements.DESCRIPTION)) {
			this.setDescription(value.getString());
		}else if(key.equals(EnumDublinCoreElements.CONTRIBUTOR)) {
			Collection<Person> persons = ((Persons) value).getPersons();
			for(Person person : persons) {
				this.addAddressLine(person.getAddressLine());
				this.addZip(person.getZip());
				this.addCountry(person.getCountry());
			}
		}else if(key.equals(EnumDublinCoreElements.COVERAGE)) {
			this.setCoverage(value.getString());
		}else if(key.equals(EnumDublinCoreElements.DATE)) {
			this.setStartDate(((DateEvents)value).iterator().next().getStartDate());
			if(((DateEvents)value).iterator().next() instanceof EdalDateRange) {
				this.setEndDate(((EdalDateRange)((DateEvents)value).iterator().next()).getEndDate());
			}
		}else if(key.equals(EnumDublinCoreElements.FORMAT)) {
			this.setFormat(value.getString());
		}else if(key.equals(EnumDublinCoreElements.LANGUAGE)) {
			this.setLanguage(((EdalLanguage)value).getLanguage().toString());
		}else if(key.equals(EnumDublinCoreElements.RIGHTS)) {
			this.setRights(value.getString());
		}else if(key.equals(EnumDublinCoreElements.SIZE)) {
			this.setSize(((DataSize)value).getFileSize());
		}else if(key.equals(EnumDublinCoreElements.SOURCE)) {
			this.setSource(value.getString());
		}else if(key.equals(EnumDublinCoreElements.TYPE)) {
			this.setType(value.getString());
		}
	}
	public void clearStringBuilders() {
		this.legalName = new StringBuilder();
		this.sureName = new StringBuilder();
		this.givenName = new StringBuilder();
		this.addressLine = new StringBuilder();
		this.zip = new StringBuilder();
		this.country = new StringBuilder();
		this.algorithm = new StringBuilder();
		this.checkSum = new StringBuilder();
		this.subjects = new StringBuilder();
		this.relations = new StringBuilder();
	}
	public String getGivenName() {
		return this.givenName.toString();
	}
	public String getSureName() {
		return sureName.toString();
	}
	public String getCountry() {
		return country.toString();
	}
	public String getZip() {
		return zip.toString();
	}
	public String getLegalName() {
		return legalName.toString();
	}
	public String getSubjects() {
		return subjects.toString();
	}
	public String getCheckSum() {
		return checkSum.toString();
	}
	public String getAlgorithm() {
		return algorithm.toString();
	}
	
	
}
