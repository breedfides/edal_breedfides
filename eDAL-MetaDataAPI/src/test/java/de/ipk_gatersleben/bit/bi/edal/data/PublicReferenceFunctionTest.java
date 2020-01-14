/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.data;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * JUnit-Test to test the {@link PublicReference} function.
 * 
 * @author arendd
 */
public class PublicReferenceFunctionTest extends EdalDefaultTestCase {

	/**
	 * Test the store function for {@link PublicReference} objects.
	 */
	@Test
	public void testStorePublicRefrences() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager
				.getRootDirectory(EdalHelpers
						.getFileSystemImplementationProvider(true,
								this.configuration), EdalHelpers
						.authenticateSampleUser());

		MetaData metadata = rootDirectory.getMetaData().clone();

		Persons creators = new Persons();

		creators.add(new NaturalPerson("arend", "daniel", "gatersleben",
				"01234", "germany"));

		metadata.setElementValue(EnumDublinCoreElements.CREATOR, creators);

		LegalPerson publisher = new LegalPerson("IPK", "gatersleben", "01234",
				"germany");

		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, publisher);

		rootDirectory.setMetaData(metadata);

		// try to add all types of persistent identifier -> successful
		for (final PersistentIdentifier id : PersistentIdentifier.values()) {
			try {
				rootDirectory.addPublicReference(id);
			} catch (final PrimaryDataEntityException e) {
				Assertions.fail(e.getMessage());
			}
		}

		// try to add again all types of persistent identifier -> fail
		for (final PersistentIdentifier id : PersistentIdentifier.values()) {
			try {
				rootDirectory.addPublicReference(id);
			} catch (final PrimaryDataEntityException e) {
				Assertions.assertTrue(true,e.getMessage());
			}
		}
	}
}