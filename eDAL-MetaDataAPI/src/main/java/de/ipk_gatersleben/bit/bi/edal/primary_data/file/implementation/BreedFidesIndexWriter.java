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
import java.nio.file.Path;
import java.util.Calendar;
import java.util.StringJoiner;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class BreedFidesIndexWriter {

	private static final String PARENT_DIRECTORY = "parentDirectory";
	private static final String REVISION = "revision";
	private static final String PRIMARY_ENTITY_ID = "primaryEntityId";

	/** The max number of bytes for the indexing of file content **/
	private final int MAX_DOC_SIZE = 100 * 1024 * 1024;

	private Analyzer analyzer = null;

	private IndexWriter indexWriter = null;
	private IndexSearcher searcher = null;
	private IndexReader reader = null;

	private DirectoryTaxonomyWriter taxonomyWriter = null;

	/** high value fetch objects faster, but more memory is needed */
	private final int FETCH_SIZE = (int) Math.pow(10, 5);
	private final int DIRECTORY_FETCH_SIZE = (int) Math.pow(10, 5);

	private DirectoryReader directoryReader;

	private Logger implementationProviderLogger = null;
	private Path indexDirectory;
	private SessionFactory sessionFactory;

	public BreedFidesIndexWriter(SessionFactory sessionFactory, Path indexDirectory,
			Logger implementationProviderLogger, IndexWriter indexWriter, DirectoryTaxonomyWriter taxonomyWriter) {

		this.implementationProviderLogger = implementationProviderLogger;
		this.indexDirectory = indexDirectory;
		this.sessionFactory = sessionFactory;
		this.indexWriter = indexWriter;
		this.analyzer = indexWriter.getAnalyzer();

		try {
			this.taxonomyWriter = taxonomyWriter;
			this.reader = DirectoryReader.open(indexWriter);
			this.searcher = new IndexSearcher(reader);
			this.directoryReader = DirectoryReader.open(indexWriter);
		} catch (IOException e) {
			this.implementationProviderLogger
					.debug("Error occured while starting the PublicVersionIndexWriterThread (opening Lucene IO tools): "
							+ e.getMessage());
		}

	}

	public void executeIndexing(PrimaryDataEntity entityToIndex) {

		if (!this.sessionFactory.isClosed()) {

			/**
			 * open new Indexreader and Indexsearcher, if the index changed since last
			 * executeIndexing() call
			 **/
			IndexReader newReader = null;
			try {
				newReader = DirectoryReader.openIfChanged(directoryReader);
				if (newReader != null) {
					this.reader.close();
					this.reader = newReader;
					this.searcher = new IndexSearcher(reader);
				}
			} catch (IOException e) {
				this.implementationProviderLogger
						.debug("There was an error when opening a new DirectoryReader: " + e.getMessage());
			}
			final Session session = this.sessionFactory.openSession();

			session.setDefaultReadOnly(true);

			/**
			 * Caching for Hibernate entities disabled because they are only used once and
			 * to prevent running out of memory
			 */
			session.setCacheMode(CacheMode.IGNORE);

			System.out.println("Entity to index: " + entityToIndex);

			/** index given entity */
			indexEntity(entityToIndex, session);

			try {
				((FileSystemImplementationProvider) DataManager.getImplProv())
						.getSearcherTaxonomyManagerForPublicReferences().maybeRefresh();
			} catch (IOException e) {
				this.implementationProviderLogger
						.debug("Error while refreshing the SearcherManager: " + e.getMessage());
			}

			try {
				this.indexWriter.commit();
				this.taxonomyWriter.commit();
			} catch (IOException e) {
				this.implementationProviderLogger
						.debug("Error while commiting Index/Taxonomy writer " + e.getMessage());
			}

			try {
				if (newReader != null)
					newReader.close();
			} catch (IOException e) {
				this.implementationProviderLogger.debug("Error closing the IndexReader: " + e.getMessage());
			}

		}
	}

	/**
	 * Traverses all files/directories that belong to a PublicReference with a stack
	 * to index data.
	 * 
	 * @param pubRef     The PublicReference of which the files are to be indexed
	 * @param internalId The InternalId of the publicreference
	 * @param session    The given Hibernate session
	 */

	private void indexEntity(PrimaryDataEntity entityToIndex, Session session) {

		try {
			this.indexVersion((PrimaryDataEntityVersionImplementation) entityToIndex.getCurrentVersion());
		} catch (MetaDataException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Uses and analyzes existing Lucene fields of a Document to add FacetFields to
	 * the Doc that allow faceted searching.
	 * 
	 * @param doc The Lucene document that must contain fields like (Description,
	 *            title..)
	 * @throws IOException
	 */
	private void addFacets(Document doc) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream(EnumIndexField.DESCRIPTION.value(),
				doc.get(EnumIndexField.DESCRIPTION.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.DESCRIPTION.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = analyzer.tokenStream(EnumIndexField.TITLE.value(), doc.get(EnumIndexField.TITLE.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.TITLE.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = analyzer.tokenStream(EnumIndexField.SUBJECT.value(), doc.get(EnumIndexField.SUBJECT.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.SUBJECT.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();

		String[] strings = doc.getValues(EnumIndexField.CREATORNAME.value());
		for (String s : strings) {
			tokenStream = analyzer.tokenStream(EnumIndexField.CREATORNAME.value(), s);
			tokenStream.reset();
			StringJoiner creator = new StringJoiner(EnumIndexing.DELIMITER.value());
			while (tokenStream.incrementToken()) {
				CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
				String token = termAttribute == null ? "" : termAttribute.toString();
				if (token.length() > 1)
					creator.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			tokenStream.close();
			if (creator.length() > 0) {
				doc.add(new FacetField(EnumIndexField.CREATORNAME.value(), creator.toString()));
			}
		}
		strings = doc.getValues(EnumIndexField.CONTRIBUTORNAME.value());
		for (String s : strings) {
			tokenStream = analyzer.tokenStream(EnumIndexField.CONTRIBUTORNAME.value(), s);
			tokenStream.reset();
			StringJoiner contributor = new StringJoiner(EnumIndexing.DELIMITER.value());
			while (tokenStream.incrementToken()) {
				CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
				String token = termAttribute == null ? "" : termAttribute.toString();
				if (token.length() > 1)
					contributor.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			tokenStream.close();
			if (contributor.length() > 0) {
				doc.add(new FacetField(EnumIndexField.CONTRIBUTORNAME.value(), contributor.toString()));
			}
		}
		strings = doc.getValues(EnumIndexField.STARTDATE.value());
		for (String s : strings) {
			doc.add(new FacetField(EnumIndexField.STARTDATE.value(), s));
		}
		doc.add(new FacetField(EnumIndexField.SIZE.value(), doc.get(EnumIndexField.SIZE.value())));
	}

	/**
	 * Getter for the IndexReader
	 * 
	 * @return The IndexReader
	 */
	public IndexReader getReader() {
		return reader;
	}

	/**
	 * Creates a Document with the information of the provided
	 * PrimaryDataEntityVersionImplementation and adds it to the index
	 * 
	 * @param version The given PrimaryDataEntityVersionImplementation
	 * @throws MetaDataException
	 */
	private void indexVersion(PrimaryDataEntityVersionImplementation version) throws MetaDataException {

		MetaData metadata = version.getMetaData();

		Document document = new Document();
		document.add(new TextField(EnumIndexField.TITLE.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.TITLE)), Store.YES));
		document.add(new TextField(EnumIndexField.DESCRIPTION.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.DESCRIPTION)), Store.YES));
		document.add(new TextField(EnumIndexField.COVERAGE.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.COVERAGE)), Store.NO));
		document.add(new TextField(EnumIndexField.IDENTIFIER.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()),
				Store.NO));
		document.add(new TextField(EnumIndexField.RELATEDIDENTIFIERTYPE.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER))
						.getRelatedIdentifierType().value()),
				Store.NO));
		document.add(new TextField(EnumIndexField.RELATIONTYPE.value(), getString(
				((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()),
				Store.NO));

		document.add(
				new TextField(EnumIndexField.SIZE.value(),
						String.format("%014d",
								((DataSize) metadata.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize()),
						Store.YES));

		document.add(new TextField(EnumIndexField.LANGUAGE.value(), getString(
				((EdalLanguage) metadata.getElementValue(EnumDublinCoreElements.LANGUAGE)).getLanguage().toString()),
				Store.NO));

		StringBuilder builder = new StringBuilder();

		Persons creators = (Persons) metadata.getElementValue(EnumDublinCoreElements.CREATOR);
		for (Person currentPerson : creators) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName() + EnumIndexing.DELIMITER.value());
				builder.append(((NaturalPerson) currentPerson).getSureName() + EnumIndexing.DELIMITER.value());
				document.add(new TextField(EnumIndexField.CREATORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append((currentPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getZip() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getCountry());
			document.add(new TextField(EnumIndexField.CREATOR.value(), builder.toString(), Store.YES));
		}
		Persons persons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
		LegalPerson legalPerson = (LegalPerson) metadata.getElementValue(EnumDublinCoreElements.PUBLISHER);
		builder.setLength(0);
		builder.append((legalPerson).getLegalName() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getZip() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getCountry());
		document.add(new TextField(EnumIndexField.LEGALPERSON.value(), builder.toString(), Store.YES));
		/**
		 * Stringbuilder to combine multiple Values into one large String to store the
		 * text in one field per categopry Not used for Relations and dates, because
		 * these values occur rarely more than once per Version/Document
		 */
		for (Person currentPerson : persons) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(EnumIndexing.DELIMITER.value());
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(EnumIndexing.DELIMITER.value());
				document.add(new TextField(EnumIndexField.CONTRIBUTORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append((currentPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getZip() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getCountry());
			document.add(new TextField(EnumIndexField.CONTRIBUTOR.value(), builder.toString(), Store.YES));
		}
		CheckSum checkSums = (CheckSum) metadata.getElementValue(EnumDublinCoreElements.CHECKSUM);
		builder.setLength(0);
		if (checkSums.size() > 1) {
			for (CheckSumType checkSum : checkSums) {
				builder.append(checkSum.getAlgorithm() + EnumIndexing.DELIMITER.value());
				builder.append(checkSum.getCheckSum());
				builder.append(", ");
				document.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getAlgorithm(), Store.NO));
			}
		} else if (checkSums.size() == 1) {
			CheckSumType checkSum = checkSums.iterator().next();
			document.add(new TextField(EnumIndexField.ALGORITHM.value(), checkSum.getAlgorithm(), Store.NO));
			document.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getCheckSum(), Store.NO));
		}
		Subjects subjects = (Subjects) metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
		builder.setLength(0);
		for (UntypedData subject : subjects) {
			builder.append(subject.getString() + EnumIndexing.DELIMITER.value());
		}
		document.add(new TextField(EnumIndexField.SUBJECT.value(), builder.toString(), Store.YES));
		builder.setLength(0);
		builder.append(
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()));
		builder.append(EnumIndexing.DELIMITER.value());
		builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER))
				.getRelatedIdentifierType().value()));
		builder.append(EnumIndexing.DELIMITER.value());
		builder.append(getString(
				((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()));
		builder.append(", ");
		document.add(new TextField(EnumIndexField.RELATION.value(), builder.toString(), Store.NO));
		DateEvents events = (DateEvents) metadata.getElementValue(EnumDublinCoreElements.DATE);
		for (EdalDate date : events) {
			document.add(new TextField(EnumIndexField.STARTDATE.value(),
					Integer.toString(date.getStartDate().get(Calendar.YEAR)), Store.YES));
			if (date instanceof EdalDateRange) {
				document.add(new TextField(EnumIndexField.ENDDATE.value(),
						Integer.toString(((EdalDateRange) date).getEndDate().get(Calendar.YEAR)), Store.YES));
			}
		}
		if (metadata.getElementValue(EnumDublinCoreElements.FORMAT) instanceof EmptyMetaData) {
			document.add(new TextField(EnumIndexField.MIMETYPE.value(), EnumIndexing.NONE.value(), Store.YES));
			document.add(new TextField(EnumIndexField.TYPE.value(), EnumIndexing.NONE.value(), Store.YES));
		} else {
			document.add(new TextField(EnumIndexField.MIMETYPE.value(),
					getString(((DataFormat) metadata.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType()),
					Store.YES));
			document.add(new TextField(EnumIndexField.TYPE.value(),
					metadata.getElementValue(EnumDublinCoreElements.TYPE).toString(), Store.YES));
		}
		StringJoiner allFieldsJoiner = new StringJoiner(EnumIndexing.DELIMITER.value());
		for (IndexableField field : document.getFields()) {
			allFieldsJoiner.add(field.stringValue());
		}
		document.add(new TextField(EnumIndexField.ALL.value(), allFieldsJoiner.toString(), Store.YES));
		document.add(new StringField(EnumIndexField.VERSIONID.value(), Integer.toString(version.getId()), Store.YES));
		document.add(new StringField(EnumIndexField.PRIMARYENTITYID.value(), version.getPrimaryEntityId(), Store.YES));
		document.add(new StringField(EnumIndexField.REVISION.value(), Long.toString(version.getRevision()), Store.YES));
		builder.setLength(0);
		Calendar cd = version.getCreationDate();
		// needed to access the related local file for content indexing if this Version
		// belongs to a file
		document.add(new StringField(EnumIndexField.CREATION_DATE.value(),
				builder.append(cd.get(Calendar.YEAR)).append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.MONTH))
						.append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.DAY_OF_MONTH))
						.append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.HOUR_OF_DAY))
						.append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.MINUTE)).toString(),
				Store.YES));
		try {

			addFacets(document);

			this.indexWriter.addDocument(((FileSystemImplementationProvider) DataManager.getImplProv())
					.getFacetsConfig().build(this.taxonomyWriter, document));

//			this.indexWriter.addDocument(doc);

		} catch (IOException e) {
			this.implementationProviderLogger.debug("Error when adding Document to IndexWriter" + e.getMessage());
		}
		document = null;
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * 
	 * @param data The given UntypedData
	 * @return The extracted String
	 */
	private String getString(UntypedData data) {
		String string = data.getString();
		return string == null ? new String() : string;
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * 
	 * @param data The given UntypedData
	 * @return The extracted String
	 */
	private String getString(String string) {
		return string == null ? "" : string;
	}
}