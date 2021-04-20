/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;
import javax.swing.JOptionPane;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

@SuppressWarnings("unused")
public class Test {
	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "arendd@ipk-gatersleben.de";

	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";

	private static Map<String, Set<String>> accessMap = new HashMap<String, Set<String>>();

	private static Map<String, Long> downloadedVolume = new HashMap<String, Long>();

	private static Map<String, Long> accessNumbers = new HashMap<String, Long>();

	private static Map<String, Long[]> accessStatistic = new HashMap<String, Long[]>();

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration(DATACITE_USERNAME, DATACITE_PASSWORD, DATACITE_PREFIX,
				new InternetAddress(EMAIL), new InternetAddress(EMAIL), new InternetAddress(EMAIL),
				new InternetAddress(ROOT_USER),"imap.ipk-gatersleben.de","","");
		configuration.setUseSSL(true);

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		PrimaryDataFile file = rootDirectory.createPrimaryDataFile("TestFile.txt");

		MetaData metadata = file.getMetaData().clone();

		IdentifierRelation idr = new IdentifierRelation();

		idr.add(new Identifier("meineID", RelatedIdentifierType.DOI, RelationType.IsSupplementTo));

		metadata.setElementValue(EnumDublinCoreElements.RELATION, idr);
		
		Persons creators = new Persons();
		creators.add(new NaturalPerson("Daniel", "Arend", "Adresse", "zip", "Land"));
		
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, creators);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, new LegalPerson("IPK", "Gatersleben", "Post", "Land"));
		
		file.setMetaData(metadata);
		
		file.addPublicReference(PersistentIdentifier.DOI);
		
		file.getCurrentVersion().setAllReferencesPublic(new InternetAddress(EMAIL));

//		DataManager.shutdown();

	}
}
