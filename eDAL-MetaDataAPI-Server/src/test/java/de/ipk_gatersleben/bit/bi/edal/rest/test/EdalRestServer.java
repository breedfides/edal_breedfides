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
package de.ipk_gatersleben.bit.bi.edal.rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalRestServer {

	public static ImplementationProvider implProv = null;
	private static File OUTPUTFILES = new File(System.getProperty("user.home") + File.separatorChar + "output");

	public static PrimaryDataDirectory startRestServer(EdalConfiguration config)
			throws AddressException, EdalConfigurationException, PrimaryDataDirectoryException,
			EdalAuthenticateException, MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			CloneNotSupportedException, PrimaryDataEntityException, PublicReferenceException, IOException {
		EdalConfiguration configuration;
		if (config == null) {
			configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
					new InternetAddress("ralfs@ipk-gatersleben.de"), new InternetAddress("ralfs@ipk-gatersleben.de"),
					new InternetAddress("ralfs@ipk-gatersleben.de"), new InternetAddress("ralfs@ipk-gatersleben.de"));
		} else {
			configuration = config;
		}
		configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);
		configuration.setHttpPort(6789);

		configuration.setUseSSL(false);

		EdalRestServer.implProv = new FileSystemImplementationProvider(configuration);

		PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv,
				EdalHelpers.authenticateWinOrUnixOrMacUser());
//		EdalHttpServer server = DataManager.getHttpServer();
//		
////		PrimaryDataDirectory dir = (PrimaryDataDirectory) root.getPrimaryDataEntity("AtomRoot");
////		
////		loadDir(dir);
//
//		HandlerCollection currentHandler = server.getHandlers();
//
//		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		restHandler.setContextPath("/restfull");
//		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//		jerseyServlet.setInitOrder(0);
//		jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
//				"de.ipk_gatersleben.bit.bi.edal.rest.server");
//		jerseyServlet.setInitParameter("javax.ws.rs.Application", "de.ipk_gatersleben.bit.bi.edal.rest.server.CustomApplication");
//		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames","de.ipk_gatersleben.bit.bi.edal.rest.server.CORSFilter,org.glassfish.jersey.media.multipart.MultiPartFeature");
//
//
//		currentHandler.prependHandler(restHandler);
//		try {
//			restHandler.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return root;
		// uploadZip(root, "Dataset12", "Ralfs1");
	}

	public static void main(String[] args)
			throws AddressException, EdalConfigurationException, PrimaryDataDirectoryException,
			EdalAuthenticateException, MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			CloneNotSupportedException, PrimaryDataEntityException, PublicReferenceException, IOException {

		startRestServer(null);

	}

	private static void loadDir(final PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException, PrimaryDataFileException, FileNotFoundException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {

				if (!primaryDataEntity.isDirectory()) {
					PrimaryDataFile file = (PrimaryDataFile) primaryDataEntity;

					File tmp = new File(OUTPUTFILES.getAbsolutePath() + File.separatorChar + primaryDataEntity);

					if (!tmp.exists()) {
						try {
							tmp.getParentFile().mkdirs();
							tmp.createNewFile();
						} catch (IOException e) {
							System.out.println("couldn't create File");
						}
					}

					FileOutputStream outputStream = new FileOutputStream(tmp);
					file.read(outputStream);

				}
				if (primaryDataEntity.isDirectory()) {
					OUTPUTFILES = new File(OUTPUTFILES.getAbsolutePath() + File.separatorChar + primaryDataEntity);
					loadDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
			OUTPUTFILES = new File(OUTPUTFILES.getParentFile().getAbsolutePath());
		}
	}

	public static void uploadZip(PrimaryDataDirectory currentDirectory, String title, String author)
			throws MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			PrimaryDataDirectoryException, CloneNotSupportedException, PrimaryDataEntityException, AddressException,
			PublicReferenceException, IOException {
		PrimaryDataDirectory entity = currentDirectory.createPrimaryDataDirectory("100mb dateien 2012");
		MetaData metadata = entity.getMetaData().clone();
		Persons persons = new Persons();
		NaturalPerson np = new NaturalPerson("Peter", author,
				"2Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstrasse 3",
				"06466", "Germany");
		persons.add(np);
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
						"IPK Gatersleben, Seeland OT Gatersleben, Corrensstrasse 3", "06466", "Germany"));

		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("wheat"));
		subjects.add(new UntypedData("transcriptional network"));
		subjects.add(new UntypedData("genie3"));
		subjects.add(new UntypedData("transcriptomics"));
		EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
		metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
		metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(title));
		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
				"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
		entity.setMetaData(metadata);
		entity.addPublicReference(PersistentIdentifier.DOI);
		// entity.getCurrentVersion().setAllReferencesPublic(new
		// InternetAddress("ralfs@ipk-gatersleben.de"));

	}

}
