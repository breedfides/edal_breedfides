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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.ehcache.CacheManager;
import org.ehcache.Status;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.schema.TargetType;

import de.ipk_gatersleben.bit.bi.edal.breedfides.persistence.Certificate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.HttpServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProviderBreedFides;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyCheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyCheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDirectoryMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyLegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyNaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPersons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MySubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUnknownMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * FileSystem implementation of {@link ImplementationProvider}
 * 
 * @author arendd
 */
public class FileSystemImplementationProvider implements ImplementationProvider {


	private static final String EDALDB_DBNAME = "edaldb";

	private Logger logger = null;

	private CountDownLatch countDownLatch = null;

	private PublicVersionIndexWriterThread publicVersionWriter;

	private static final int SQL_ERROR_DATABASE_IN_USE = 90020;

	private static final int SQL_ERROR_DATABASE_NOT_FOUND = 90013;

	public static final List<String> STOPWORDS = Arrays.asList("a", "about", "again", "all", "almost", "also",
			"although", "always", "among", "an", "and", "another", "any", "are", "as", "at", "be", "because", "been",
			"before", "being", "between", "both", "but", "by", "can", "could", "did", "do", "does", "done", "due",
			"during", "e!DAL", "each", "either", "enough", "especially", "etc", "for", "found", "from", "further",
			"had", "has", "have", "having", "here", "how", "however", "i", "if", "in", "into", "is", "it", "its",
			"itself", "just", "kg", "km", "made", "mainly", "make", "may", "mg", "might", "ml", "mm", "most", "mostly",
			"must", "nearly", "neither", "no", "nor", "obtained", "of", "often", "on", "our", "overall", "perhaps",
			"pmid", "quite", "rather", "really", "regarding", "seem", "seen", "several", "should", "show", "showed",
			"shown", "shows", "significantly", "since", "so", "some", "such", "than", "that", "the", "their", "theirs",
			"them", "then", "there", "therefore", "these", "they", "this", "those", "through", "thus", "to", "upon",
			"using", "various", "very", "was", "we", "were", "what", "when", "which", "while", "with", "within",
			"without", "would");

	private IndexWriter indexWriter = null;

	private boolean hibernateIndexing = false;

	private boolean autoIndexing;

	private EdalConfiguration configuration;
	private Connection connection = null;

	private String databasePassword;
	private String databaseUsername;
	private IndexWriterThread indexThread = null;
	private SessionFactory sessionFactory = null;
	private Path indexDirectory = null;
	private CacheManager cacheManager = null;
	
	private static final String FACETS_PUBLIC = "Facets_Public";
	private Directory facetDirectoryForPublicReferences = null;
	private DirectoryTaxonomyWriter taxoWriterForPublicReferences = null;
	
	private static final String FACETS_NATIVE = "Facets_Native";
	private Directory facetDirectoryForNativeEntities = null;
	private DirectoryTaxonomyWriter taxoWriterForNativeEntities = null;

	public static final String MASTER_INDEX = "Master_Index";

	/**
	 * Manages the reopining and sharing of IndexSearcher instances
	 */
	private static SearcherTaxonomyManager SearcherTaxonomyManagerForPublicReferences;
	
	private static SearcherTaxonomyManager SearcherTaxonomyManagerForNative;


	/**
	 * Used for indexing of Facets, holds information about the indexed
	 * Facet-dimensions
	 **/
	private static FacetsConfig factesConfig = new FacetsConfig();
	
	private static final String FACETS_BREEDFIDES = "Facets_BreedFides";
	private BreedFidesIndexWriter breedFidesIndexWriter;
	private Directory facetDirectoryForBreedFidesEntities;
	private DirectoryTaxonomyWriter taxoWriterForBreedFides;

	public FileSystemImplementationProvider(EdalConfiguration configuration) {

		this.configuration = configuration;

		factesConfig.setMultiValued(EnumIndexField.CREATORNAME.value(), true);
		factesConfig.setMultiValued(EnumIndexField.CONTRIBUTORNAME.value(), true);
		factesConfig.setMultiValued(EnumIndexField.SUBJECT.value(), true);
		factesConfig.setMultiValued(EnumIndexField.TITLE.value(), true);
		factesConfig.setMultiValued(EnumIndexField.DESCRIPTION.value(), true);
		factesConfig.setMultiValued(EnumIndexField.STARTDATE.value(), true);


		try {
			this.setDatabaseUsername(this.getConfiguration().getDatabaseUsername());
			this.setDatabasePassword(this.getConfiguration().getDatabasePassword());
		} catch (EdalConfigurationException e) {
			// should never happen, because of the validation function
			e.printStackTrace();
		}

		this.logger = configuration.getLogger();

		this.setAutoIndexing(autoIndexing);

		try {
			try {
				Class.forName("org.h2.Driver");
				this.setConnection(DriverManager.getConnection(
						"jdbc:h2:split:30:" + this.getMountPath() + ";DB_CLOSE_ON_EXIT=FALSE",
						this.getDatabaseUsername(), this.getDatabasePassword()));

				this.getLogger().info("Database connection established");
			} catch (final ClassNotFoundException e) {
				this.getLogger().error("Could not find driver for H2 connection !");
				System.exit(0);
			}

		} catch (final SQLException se) {

			if (se.getErrorCode() == SQL_ERROR_DATABASE_IN_USE) {
				this.getLogger().warn("Database still in use -> close and restart please !");
				System.exit(0);
			}
			if (se.getErrorCode() == SQL_ERROR_DATABASE_NOT_FOUND) {
				this.getLogger().info("No database found -> creating new database...");
				try {
					this.setConnection(DriverManager.getConnection(
							"jdbc:h2:split:30:" + this.getMountPath() + ";DB_CLOSE_ON_EXIT=FALSE",
							this.getDatabaseUsername(), this.getDatabasePassword()));

				} catch (final SQLException e) {
					this.getLogger().error("Could not start H2 connection !");
					System.exit(0);
				}
			}
		}

		this.indexDirectory = Paths.get(this.getMountPath().toString(), "lucene");

		final Configuration config = new Configuration();

		config.configure(FileSystemImplementationProvider.class.getResource("hibernate.cfg.xml"));

		XmlConfiguration xmlconfig = new XmlConfiguration(getClass().getResource("ehcache.cfg.xml"));

		CacheManager ehcacheManager = CacheManagerBuilder.newCacheManager(xmlconfig);

		ehcacheManager.init();

		this.setCacheManager(ehcacheManager);

		config.setProperty("hibernate.connection.url",
				"jdbc:h2:split:30:" + this.getMountPath() + ";DB_CLOSE_ON_EXIT=FALSE");
		config.setProperty("hibernate.connection.username", this.getDatabaseUsername());
		config.setProperty("hibernate.connection.password", this.getDatabasePassword());
		// config.setProperty("hibernate.search.backend.exclusive_index_use", "false");
		config.setProperty("hibernate.search.backend.directory.root", this.indexDirectory.toString());

		if (!this.isAutoIndexing()) {
			config.setProperty("hibernate.search.automatic_indexing.strategy", "none");
		}

		Boolean exists = false;
		try (ResultSet result = this.getConnection().createStatement().executeQuery("SELECT count(*) FROM ENTITIES ")) {
			result.last();
			final int resultSize = result.getInt("COUNT(*)");
			if (resultSize > 0) {
				exists = true;
			}
			result.close();
		} catch (final SQLException e) {
			exists = false;
		}

		List<Class<?>> annotatedHibernateClasses = Arrays.asList(RootImplementation.class,
				PrincipalImplementation.class, PrimaryDataDirectoryImplementation.class,
				PrimaryDataFileImplementation.class, PrimaryDataEntityVersionImplementation.class,
				MetaDataImplementation.class, EdalPermissionImplementation.class, SupportedPrincipals.class,
				PublicReferenceImplementation.class, TicketImplementation.class, ReviewersImplementation.class,
				ReviewStatusImplementation.class, UrlImplementation.class, DoiImplementation.class, MyDataFormat.class,
				MyDataSize.class, MyDataType.class, MyDirectoryMetaData.class, MyEmptyMetaData.class,
				MyIdentifier.class, MyIdentifierRelation.class, MyPersons.class, MyPerson.class, MyNaturalPerson.class,
				MyLegalPerson.class, MyUnknownMetaData.class, MyUntypedData.class, MySubjects.class,
				MyCheckSumType.class, MyCheckSum.class, MyEdalLanguage.class, MyEdalDate.class, MyEdalDateRange.class,
				MyDateEvents.class, MyORCID.class, Certificate.class);

		if (!exists) {

			StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
					.applySettings(config.getProperties()).build();

			MetadataSources metadata = new MetadataSources(standardRegistry);

			for (Class<?> annotatedClass : annotatedHibernateClasses) {
				metadata.addAnnotatedClass(annotatedClass);
			}

			SchemaExport export = new SchemaExport();
			export.setDelimiter(";");
			export.setFormat(true);
			EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE);

			export.createOnly(targetTypes, metadata.buildMetadata());

			try {
				Metadata meta = metadata.buildMetadata();
				this.setSessionFactory(meta.getSessionFactoryBuilder().build());
			} catch (HibernateException e) {
				e.printStackTrace();
				logger.error("Lucene Index damaged", e);
				logger.info("Lucene Index damaged -> clean up index directory to rebuild the index !");
				System.exit(0);
			}
			// /*
			// * to check if the PermissionCheck for normal users save another
			// * root-users
			// */
			// final Session session = this.getSessionFactory().openSession();
			// final Transaction transaction = session.beginTransaction();
			// try {
			// session.save(new RootImplementation("D", "A",
			// new InternetAddress("edal-root@ipk-gatersleben.de")));
			// } catch (AddressException e) {
			// e.printStackTrace();
			// }
			// transaction.commit();
			// session.close();
			// /* ********************************************************** */
		} else {
			StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
					.applySettings(config.getProperties()).build();

			MetadataSources metadata = new MetadataSources(standardRegistry);

			for (Class<?> annotatedClass : annotatedHibernateClasses) {
				metadata.addAnnotatedClass(annotatedClass);
			}

			try {
				Metadata meta = metadata.getMetadataBuilder().build();
				this.setSessionFactory(meta.getSessionFactoryBuilder().build());
			} catch (HibernateException e) {
				e.printStackTrace();
				logger.error("Lucene Index damaged", e);
				logger.info("Lucene Index damaged -> clean up index directory to rebuild the index !");
				System.exit(0);
			}
			/* validate database schema */
			try {
				SchemaValidator sv = new SchemaValidator();

				sv.validate(metadata.buildMetadata());

				this.getLogger().info("Database Schema Validation : successful");

			} catch (final HibernateException e) {

				e.printStackTrace();
				this.getLogger().error("Found existing, but not compatible database schema in path '"
						+ configuration.getMountPath() + "' (" + e.getMessage() + ") ");
				this.getLogger().error("Please delete path or specify another mount path !");
				System.exit(0);

//				this.getLogger().warn("Running Schema-Update");
//
//				SchemaUpdate su = new SchemaUpdate();
//				
//				EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE);
//
//				su.setHaltOnError(false);
//				
//				su.execute(targetTypes, metadata.buildMetadata());
//				
//				while (su.getExceptions().iterator().hasNext()) {
//					((Exception)su.getExceptions().iterator().next()).printStackTrace();
//				}
//				
//				System.exit(0);

			}
		}

		/* enable statistics log of the SessionFactory */
		this.getSessionFactory().getStatistics().setStatisticsEnabled(true);

		if (!this.isAutoIndexing()) {
			List<IndexWriterThread> indexWriterThreads = new ArrayList<>();
			if (this.configuration.isHibernateSearchIndexingEnabled()) {

				indexWriterThreads.add(
						new HibernateIndexWriterThread(this.getSessionFactory(), this.indexDirectory, this.logger));
				this.countDownLatch = new CountDownLatch(indexWriterThreads.size());
				indexWriterThreads.get(0).setCountDownLatch(this.countDownLatch);
				this.setIndexThread(indexWriterThreads.get(0));
				this.getIndexThread().start();
			} else {
				try {
					Directory indexingDirectory = FSDirectory.open(Paths.get(indexDirectory.toString(), MASTER_INDEX));
					TieredMergePolicy pol = new TieredMergePolicy();
					CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
					final CharArraySet stopSet = new CharArraySet(STOPWORDS.size() + defaultStopWords.size(), false);
					stopSet.addAll(defaultStopWords);
					stopSet.addAll(STOPWORDS);
					IndexWriterConfig writerConfig = new IndexWriterConfig(
							new StandardAnalyzer(CharArraySet.unmodifiableSet(stopSet)));
					writerConfig.setMergePolicy(pol);
					this.indexWriter = new IndexWriter(indexingDirectory, writerConfig);
					this.facetDirectoryForPublicReferences = FSDirectory.open(Paths.get(this.indexDirectory.toString(), FACETS_PUBLIC));
					this.taxoWriterForPublicReferences = new DirectoryTaxonomyWriter(this.facetDirectoryForPublicReferences);
					
					this.facetDirectoryForNativeEntities = FSDirectory.open(Paths.get(this.indexDirectory.toString(), FACETS_NATIVE));
					this.taxoWriterForNativeEntities = new DirectoryTaxonomyWriter(this.facetDirectoryForNativeEntities);
					
				} catch (IOException e) {
					e.printStackTrace();
					this.getLogger().error("System wasn't able to create/open Lucene Index!");
					System.exit(0);
				}
				indexWriterThreads.add(new NativeLuceneIndexWriterThread(this.getSessionFactory(), this.indexDirectory,
						this.logger, this.indexWriter, this.taxoWriterForNativeEntities));
				indexWriterThreads.add(new PublicVersionIndexWriterThread(this.getSessionFactory(), this.indexDirectory,
						this.logger, this.indexWriter, this.taxoWriterForPublicReferences));
				this.countDownLatch = new CountDownLatch(indexWriterThreads.size());
				indexWriterThreads.get(0).setCountDownLatch(this.countDownLatch);
				indexWriterThreads.get(1).setCountDownLatch(this.countDownLatch);
				((PublicVersionIndexWriterThread) indexWriterThreads.get(1)).setTestMode(configuration.isInTestMode());
				this.setIndexThread(indexWriterThreads.get(0));
				this.setPublicVersionWriter((PublicVersionIndexWriterThread) indexWriterThreads.get(1));
//				this.getIndexThread().start();
//				this.getPublicVersionWriter().start();
				try {
					SearcherTaxonomyManagerForPublicReferences = new SearcherTaxonomyManager(this.indexWriter, new SearcherFactory(), this.taxoWriterForPublicReferences);
					SearcherTaxonomyManagerForNative = new SearcherTaxonomyManager(this.indexWriter, new SearcherFactory(), this.taxoWriterForNativeEntities);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
				this.facetDirectoryForBreedFidesEntities = FSDirectory.open(Paths.get(this.indexDirectory.toString(), FACETS_BREEDFIDES));

				this.taxoWriterForBreedFides =  new DirectoryTaxonomyWriter(this.facetDirectoryForBreedFidesEntities);
				
				this.breedFidesIndexWriter = new BreedFidesIndexWriter(this.sessionFactory, this.indexDirectory, this.logger, this.indexWriter, this.taxoWriterForBreedFides);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public MetaDataImplementation createMetaDataInstance() {
		return new MetaDataImplementation();
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends ApprovalServiceProvider> getApprovalServiceProvider() {

		return ApprovalServiceProviderImplementation.class;
	}

	/** {@inheritDoc} */
	@Override
	public EdalConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Getter for the database {@link Connection}.
	 * 
	 * @return a {@link Connection} object.
	 */
	public Connection getConnection() {
		return this.connection;
	}

	public PublicVersionIndexWriterThread getPublicVersionWriter() {
		return publicVersionWriter;
	}

	public void setPublicVersionWriter(PublicVersionIndexWriterThread publicVersionWriter) {
		this.publicVersionWriter = publicVersionWriter;
	}

	/**
	 * Private getter for the dataPath
	 * 
	 * @return the dataPath
	 */
	public Path getDataPath() {
		return Paths.get(this.getConfiguration().getDataPath().toString(),
				FileSystemImplementationProvider.EDALDB_DBNAME);
	}

	/**
	 * Getter for the database password.
	 * 
	 * @return the databasePassword
	 */
	private String getDatabasePassword() {
		return this.databasePassword;
	}

	/**
	 * Getter for the database user.
	 * 
	 * @return the databaseUsername
	 */
	private String getDatabaseUsername() {
		return this.databaseUsername;
	}

	/**
	 * @return the indexThread
	 */
	public IndexWriterThread getIndexThread() {
		return this.indexThread;
	}

	/** {@inheritDoc} */
	@Override
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Getter for the mount path of the EDAL system.
	 * 
	 * @return the current MountPath.
	 */
	public Path getMountPath() {
		return Paths.get(this.getConfiguration().getMountPath().toString(),
				FileSystemImplementationProvider.EDALDB_DBNAME);
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends PermissionProvider> getPermissionProvider() {

		return PermissionProviderImplementation.class;
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends PrimaryDataDirectory> getPrimaryDataDirectoryProvider() {

		return PrimaryDataDirectoryImplementation.class;
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataEntity reloadPrimaryDataEntityByID(final String uuid, final long versionNumber)
			throws EdalException {

		final Session session = this.getSessionFactory().openSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
				.createQuery(PrimaryDataFileImplementation.class);
		Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);

		fileCriteria.where(builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
				builder.equal(fileRoot.get("ID"), uuid)));

		final PrimaryDataFile file = session.createQuery(fileCriteria).uniqueResult();

		if (file == null) {

			CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
					.createQuery(PrimaryDataDirectoryImplementation.class);
			Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
					.from(PrimaryDataDirectoryImplementation.class);

			directoryCriteria
					.where(builder.and(builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class),
							builder.equal(directoryRoot.get("ID"), uuid)));

			final PrimaryDataDirectory directory = session.createQuery(directoryCriteria).uniqueResult();

			if (directory == null) {
				session.close();
				throw new EdalException("found no entity with ID '" + uuid + "'");
			} else {
				PrimaryDataEntityVersion version;
				try {
					version = directory.getVersionByRevisionNumber(versionNumber);
				} catch (PrimaryDataEntityVersionException e) {
					session.close();
					throw new EdalException(e.getMessage(), e);
				}

				try {
					directory.switchCurrentVersion(version);
				} catch (PrimaryDataEntityVersionException e) {
					session.close();
					throw new EdalException("unable to switch the version with the number " + versionNumber, e);
				}
			}
			session.close();
			return directory;
		} else {

			PrimaryDataEntityVersion version;
			try {
				version = file.getVersionByRevisionNumber(versionNumber);
			} catch (PrimaryDataEntityVersionException e) {
				session.close();
				throw new EdalException(e.getMessage(), e);
			}

			try {
				file.switchCurrentVersion(version);
			} catch (PrimaryDataEntityVersionException e) {
				session.close();
				throw new EdalException("unable to switch the version with the number " + versionNumber, e);
			}
			session.close();
			return file;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends PrimaryDataFile> getPrimaryDataFileProvider() {
		return PrimaryDataFileImplementation.class;
	}

	
	/**
	 * Getter for a new {@link Session} for public access.
	 * 
	 * <em>NOTE: use {@link Session#close()} after UnitOfWork !</em>
	 * 
	 * @return new Session
	 */
	public Session getSession() {
		return this.getSessionFactory().openSession();
	}

	/**
	 * Private Setter for the {@link SessionFactory}
	 * 
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * Getter for the {@link Statistics} of the {@link SessionFactory}
	 * 
	 * @return the {@link Statistics}
	 */
	public Statistics getStatistics() {
		return this.getSessionFactory().getStatistics();
	}

	/**
	 * Private getter for the autoIndexing
	 * 
	 * @return the autoIndexing
	 */
	private boolean isAutoIndexing() {
		return this.autoIndexing;
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataDirectory mount(final List<Class<? extends Principal>> supportedPrincipals)
			throws PrimaryDataDirectoryException {

		final Session session = this.getSessionFactory().openSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataDirectoryImplementation> rootDirectoryCriteria = builder
				.createQuery(PrimaryDataDirectoryImplementation.class);
		Root<PrimaryDataDirectoryImplementation> rootDirectoryRoot = rootDirectoryCriteria
				.from(PrimaryDataDirectoryImplementation.class);

		rootDirectoryCriteria
				.where(builder.and(builder.equal(rootDirectoryRoot.type(), PrimaryDataDirectoryImplementation.class),
						builder.isNull((rootDirectoryRoot.get("parentDirectory")))));

		if (session.createQuery(rootDirectoryCriteria).uniqueResult() == null) {

			session.close();

			DataManager.getImplProv().getLogger().info("Creating new RootDirectory...");

			final Session sess = this.getSessionFactory().openSession();
			final Transaction transaction = sess.beginTransaction();

			for (final Class<? extends Principal> clazz : supportedPrincipals) {
				sess.save(new SupportedPrincipals(clazz));
			}
			transaction.commit();
			sess.close();
			PrimaryDataDirectory newRootDirectory = null;

			try {
				final Constructor<? extends PrimaryDataDirectory> constructor = DataManager.getImplProv()
						.getPrimaryDataDirectoryProvider().getConstructor(PrimaryDataDirectory.class, String.class);

				newRootDirectory = constructor.newInstance(null, PrimaryDataDirectory.PATH_SEPARATOR);
			} catch (final Exception e) {
				throw new PrimaryDataDirectoryException(
						"Can not instantiate the constructor to mount implementation: " + e.getMessage(), e);
			}

			return newRootDirectory;
		}

		else {

			CriteriaQuery<SupportedPrincipals> principalCriteria = builder.createQuery(SupportedPrincipals.class);
			Root<SupportedPrincipals> principalRoot = principalCriteria.from(SupportedPrincipals.class);

			principalCriteria.select(principalRoot);

			final List<SupportedPrincipals> privatePrincipals = session.createQuery(principalCriteria).list();

			int size = supportedPrincipals.size();

			final List<SupportedPrincipals> publicPrincipals = new ArrayList<SupportedPrincipals>(size);

			for (int i = 0; i < supportedPrincipals.size(); i++) {
				publicPrincipals.add(new SupportedPrincipals(supportedPrincipals.get(i)));
			}

			if (privatePrincipals.containsAll(publicPrincipals)) {
				DataManager.getImplProv().getLogger().info("All principals are supported !");
			} else {
				DataManager.getImplProv().getLogger()
						.warn("Not all principals are supported , please define new list and connect again !");
				throw new PrimaryDataDirectoryException(
						"Not all principals are supported , please define new list and connect again !");
			}

			DataManager.getImplProv().getLogger().info("Getting existing RootDirectory...");

			final PrimaryDataDirectoryImplementation existingRootDirectory = session.createQuery(rootDirectoryCriteria)
					.uniqueResult();

			session.close();

			final PrimaryDataDirectory existingRootDirectoryOrg = existingRootDirectory;

			return existingRootDirectoryOrg;
		}

	}

	public boolean isHibernateIndexing() {
		return hibernateIndexing;
	}

	public IndexWriter getWriter() {
		return indexWriter;
	}

	public void setHibernateIndexing(boolean hibernateIndexing) {
		this.hibernateIndexing = hibernateIndexing;
	}

	/**
	 * Getter for the FacetsConfig
	 * 
	 * @return The FacetsConfig for faceted indexing
	 */
	public FacetsConfig getFacetsConfig() {
		return factesConfig;
	}

	/**
	 * Getter for the SearcherTaxonomyManager
	 * 
	 * @return The SearcherTaxonomyManager
	 */
	public SearcherTaxonomyManager getSearcherTaxonomyManagerForPublicReferences() {
		return SearcherTaxonomyManagerForPublicReferences;
	}

	public Path getIndexDirectory() {
		return this.indexDirectory;
	}

	/**
	 * Private setter for the autoIndexing
	 * 
	 * @param autoIndexing the autoIndexing to set
	 */
	private void setAutoIndexing(final boolean autoIndexing) {
		this.autoIndexing = autoIndexing;
	}

	private void setConnection(final Connection connection) {
		this.connection = connection;
	}

	/**
	 * Private setter for the database password.
	 * 
	 * @param databasePassword the database password to set
	 */
	private void setDatabasePassword(final String databasePassword) {
		this.databasePassword = databasePassword;
	}

	/**
	 * Private setter for the database user
	 * 
	 * @param databaseUsername the database username to set
	 */
	private void setDatabaseUsername(final String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	/**
	 * Private setter for the indexingThread
	 * 
	 * @param indexThread the indexThread to set
	 */
	private void setIndexThread(final IndexWriterThread indexThread) {
		this.indexThread = indexThread;
	}

	/**
	 * Private setter for the {@link SessionFactory}
	 * 
	 * @param sessionFactory the sessionFactory to set
	 */
	private void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/** {@inheritDoc} */
	@Override
	public void shutdown() {
		if (!this.isAutoIndexing()) {
			this.getIndexThread().waitForFinish();
			if (!this.configuration.isHibernateSearchIndexingEnabled()) {
				this.getPublicVersionWriter().waitForFinish();
			}
			try {
				this.getLogger().info("Waiting for indexing to finish");
				this.countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {

				if (indexWriter != null) {
					try {
						indexWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
						try {
							this.indexWriter.rollback();
							indexWriter.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					indexWriter = null;
				}
			}
		}
		try {
			this.getSessionFactory().close();
			this.getConnection().close();
			if (!this.getCacheManager().getStatus().equals(Status.UNINITIALIZED)) {
				this.getCacheManager().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Class<? extends ServiceProvider> getServiceProvider() {
		return ServiceProviderImplementation.class;
	}

	@Override
	public Class<? extends HttpServiceProvider> getHttpServiceProvider() {
		return HttpServiceProviderImplementation.class;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	private void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public Class<? extends SearchProvider> getSearchProvider() {
		return SearchProviderImplementation.class;
	}

	public SearcherTaxonomyManager getSearcherTaxonomyManagerForNative() {
		return SearcherTaxonomyManagerForNative;
	}

	@Override
	public Class<? extends SearchProviderBreedFides> getSearchProviderBreedFides() {
		return SearchProviderBreedFidesImplementation.class;
	}

	public BreedFidesIndexWriter getBreedFidesIndexWriter() {
		return breedFidesIndexWriter;
	}
}