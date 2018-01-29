/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.util.Calendar;
import java.util.Locale;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;

public class MetaDataCollector {

	private LegalPerson publisher = null;
	private UntypedData description = null;
	private UntypedData title = null;
	private EdalLanguage language = null;
	private UntypedData license = null;
	private Persons creators;
	private Persons contributors;
	private Subjects subjects;
	private Calendar embargoDate = null;

	public MetaDataCollector() {
		this.creators = new Persons();
		this.contributors = new Persons();
		this.subjects = new Subjects();

	}

	public void collectAllMetaData() {

		this.collectTitle(PublicationMainPanel.titleField.getText());
		this.collectDescription(PublicationMainPanel.descriptionField.getText());
		this.collectSubjects(PublicationMainPanel.subjectPanel.getSubjects());
		this.collectPublisher(PublicationMainPanel.publisherPanel.getPublisher());
		this.collectLanguage(PublicationMainPanel.languagePanel.getLanguage());
		this.collectLicense(PublicationMainPanel.licensePanel.getLicense());
		this.collectCreators(PublicationMainPanel.authorPanel.getCreators());
		this.collectContributors(PublicationMainPanel.authorPanel.getContributors());
		this.collectEmbargoDate(PublicationMainPanel.embargboPanel.getEmbargoDate());

	}

	private void collectEmbargoDate(final Calendar embargoDate) {
		this.embargoDate = embargoDate;
	}

	private void collectContributors(Persons contributors) {
		this.contributors = contributors;
	}

	private void collectCreators(Persons creators) {
		this.creators = creators;
	}

	private void collectDescription(final String description) {
		this.description = new UntypedData(description);
	}

	private void collectLanguage(Locale language) {
		this.language = new EdalLanguage(language);
	}

	private void collectLicense(String license) {
		this.license = new UntypedData(license);

	}

	private void collectPublisher(final LegalPerson publisher) {
		this.publisher = publisher;
	}

	private void collectSubjects(final String[] subjects) {
		for (String string : subjects) {
			this.subjects.add(new UntypedData(string));
		}
	}

	private void collectTitle(final String title) {
		this.title = new UntypedData(title);
	}

	/**
	 * @return the contributors
	 */
	public Persons getContributors() {
		return contributors;
	}

	/**
	 * @return the creators
	 */
	public Persons getCreators() {
		return creators;
	}

	/**
	 * @return the description
	 */
	public UntypedData getDescription() {
		return description;
	}

	/**
	 * @return the language
	 */
	public EdalLanguage getLanguage() {
		return language;
	}

	/**
	 * @return the license
	 */
	public UntypedData getLicense() {
		return license;
	}

	/**
	 * @return the publisher
	 */
	public LegalPerson getPublisher() {
		return publisher;
	}

	/**
	 * @return the subjects
	 */
	public Subjects getSubjects() {
		return subjects;
	}

	/**
	 * @return the title
	 */
	public UntypedData getTitle() {
		return title;
	}

	/**
	 * @return the embargo date
	 */
	public Calendar getEmbargoDate() {
		return embargoDate;
	}

	@Override
	public String toString() {
		return "MetaDataCollector [\n subjects=" + subjects + ",\n publisher=" + publisher + ",\n description="
				+ description + ",\n title=" + title + ",\n language=" + language + ",\n license=" + license
				+ ",\n creators=" + creators + ",\n contributors=" + contributors + "\n]";
	}

}
