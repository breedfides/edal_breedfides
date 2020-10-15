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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;
import javax.swing.JOptionPane;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.EdalPermissionImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

@SuppressWarnings("unused")
public class Test {
	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "user@nodomain.com.invalid";

	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";

	private static Map<String, Set<String>> accessMap = new HashMap<String, Set<String>>();

	private static Map<String, Long> downloadedVolume = new HashMap<String, Long>();

	private static Map<String, Long> accessNumbers = new HashMap<String, Long>();

	private static Map<String, Long[]> accessStatistic = new HashMap<String, Long[]>();

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration(DATACITE_USERNAME, DATACITE_PASSWORD, DATACITE_PREFIX, new InternetAddress(EMAIL),
				new InternetAddress(EMAIL), new InternetAddress(EMAIL), new InternetAddress(ROOT_USER),"imap.ipk-gatersleben.de","","");
		configuration.setUseSSL(true);

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(false, configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<PublicReferenceImplementation> cr = builder.createQuery(PublicReferenceImplementation.class);
		Root<PublicReferenceImplementation> root = cr.from(PublicReferenceImplementation.class);
		cr.select(root);
		Query<PublicReferenceImplementation> query = session.createQuery(cr);
		//List<PublicReferenceImplementation> permissions = query.getResultList();
		//final Criteria getFile = session.createCriteria(PublicReferenceImplementation.class);

		@SuppressWarnings("unchecked")
		List<PublicReferenceImplementation> list = query.getResultList();

		session.close();

		Path pathToLogFiles = Paths.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "jetty_log");

		for (File file : pathToLogFiles.toFile().listFiles()) {

			FileInputStream is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String strLine;

			while ((strLine = br.readLine()) != null) {

				String[] split = strLine.split("\t");

				if (split[5].startsWith("GET /DOI/")) {

					String publicReferenceId = split[5].split("/")[2];
					if (publicReferenceId.length() == 36) {

						if (accessMap.containsKey(split[1])) {

							accessMap.get(split[1]).add(publicReferenceId);

						} else {

							Set<String> set = new HashSet<String>();

							set.add(publicReferenceId);

							accessMap.put(split[1], set);

						}

						if (downloadedVolume.containsKey(publicReferenceId)) {

							Long temp = downloadedVolume.get(publicReferenceId) + Long.parseLong(split[7]);
							downloadedVolume.put(publicReferenceId, temp);

						} else {

							downloadedVolume.put(publicReferenceId, Long.parseLong(split[7]));
						}

					}
				}

			}

			br.close();

		}

		for (Entry<String, Set<String>> entry : accessMap.entrySet()) {

			for (String string : entry.getValue()) {

				if (accessNumbers.containsKey(string)) {
					Long number = accessNumbers.get(string) + 1;
					accessNumbers.put(string, number);
				} else {
					accessNumbers.put(string, Long.valueOf(1));
				}
			}
		}

		for (Entry<String, Long> entry : accessNumbers.entrySet()) {

			Long[] array = { entry.getValue(), downloadedVolume.get(entry.getKey()) };

			accessStatistic.put(entry.getKey(), array);

		}

		for (Entry<String, Long[]> entry : accessStatistic.entrySet()) {

			for (PublicReferenceImplementation reference : list) {

				if (reference.getInternalID().equals(entry.getKey())) {
					System.out.println(entry.getKey() + "\t" + reference.getAssignedID() + "\t" + entry.getValue()[0] + "\t" + entry.getValue()[1]);

				}
			}

			// System.out.println(entry.getKey() + "\t" + entry.getValue()[0] +
			// "\t" + entry.getValue()[1]);

		}

		// PrimaryDataDirectory directory =
		// rootDirectory.createPrimaryDataDirectory("directory");
		//
		// PrimaryDataDirectory subDirectory =
		// directory.createPrimaryDataDirectory("subDirectory");
		//
		// MetaData metadata = directory.getMetaData().clone();
		//
		// Persons persons = new Persons();
		//
		// persons.add(new NaturalPerson("d", "a", "a", "z", "c"));
		//
		// metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		// metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, new
		// LegalPerson("l", "a", "z", "c"));
		// metadata.setElementValue(EnumDublinCoreElements.RIGHTS, new
		// UntypedData("RECHTE"));
		//
		// directory.setMetaData(metadata);
		//
		// directory.addPublicReference(PersistentIdentifier.DOI);
		//
		// directory.getCurrentVersion().getPublicReference(PersistentIdentifier.DOI).setPublic(new
		// InternetAddress(EMAIL));

		// JOptionPane.showMessageDialog(null, "ENDE");

		DataManager.shutdown();

	}
}
