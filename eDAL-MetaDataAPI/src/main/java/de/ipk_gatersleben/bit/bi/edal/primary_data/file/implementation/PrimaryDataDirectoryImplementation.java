/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.queryParser.MultiFieldQueryParser;
//import org.apache.lucene.queryParser.ParseException;
//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyLegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyNaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;

/**
 * Implementation of {@link PrimaryDataDirectory}.
 * 
 * @author arendd
 */

@Entity
@Table(name = "ENTITIES")
@DiscriminatorColumn(columnDefinition = "char(1)", name = "TYPE", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("D")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "PrimaryDataDirectory")
public class PrimaryDataDirectoryImplementation extends PrimaryDataDirectory {

    private static final String STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION = "Unable to switch to current version";

    private static final String CACHE_REGION_SEARCH_ENTITY = "search.entity";

    private static final String STRING_ID = "id";

    private static final String STRING_PARENT_DIRECTORY = "parentDirectory";

    private static final String STRING_CLASS = "class";

    private static final String SUPPRESS_UNCHECKED_WARNING = "unchecked";

    /**
     * Maximal number of search result, if the number is higher the user must
     * specify a more detailed search request, otherwise the search function is
     * to slow because of HIBERNATE.
     */
    private static final int MAX_NUMBER_SEARCH_RESULTS = 1000;

    private SortedSet<PrimaryDataEntityVersionImplementation> versionList;

    /**
     * Default constructor for {@link PrimaryDataDirectoryImplementation} is
     * necessary for PojoInstantiator of <em>HIBERNATE</em>.
     */
    protected PrimaryDataDirectoryImplementation() {

    }

    /**
     * Constructor for PrimaryDataDirectoryImplementation.
     * 
     * @param path
     *            a {@link PrimaryDataDirectory} object.
     * @param name
     *            a {@link String} object.
     * @throws PrimaryDataEntityVersionException
     *             if can not set current {@link PrimaryDataEntityVersion}.
     * @throws PrimaryDataDirectoryException
     *             if no parent {@link PrimaryDataDirectory} is found.
     * @throws MetaDataException
     *             if the {@link MetaData} object of the parent
     *             {@link PrimaryDataDirectory} is not clone-able
     */
    public PrimaryDataDirectoryImplementation(final PrimaryDataDirectory path,
	    final String name) throws PrimaryDataEntityVersionException,
	    PrimaryDataDirectoryException, MetaDataException {
	super(path, name);
    }

    private PrimaryDataEntity checkIfEntityExists(final String name) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	session.setDefaultReadOnly(true);

	final SQLQuery fileQuery = session
		.createSQLQuery(
			"SELECT DISTINCT t4.ID, t4.TYPE, t4.PARENTDIRECTORY_ID FROM "
				+ "UNTYPEDDATA t1, METADATA_MAP t2, ENTITY_VERSIONS t3, ENTITIES t4 "
				+ "where t3.METADATA_ID=t2.METADATA_ID "
				+ "and t1.id=t2.MYMAP_ID and t2.MYMAP_KEY=15 "
				+ "and t1.STRING=:name and t3.PRIMARYENTITYID=t4.ID "
				+ "and t4.TYPE='F' and t4.PARENTDIRECTORY_ID=:parent")
		.addEntity(PrimaryDataFileImplementation.class);

	fileQuery.setString("name", name);
	fileQuery.setString("parent", this.getID());

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<PrimaryDataFileImplementation> files = fileQuery.list();

	for (final PrimaryDataFileImplementation file : files) {

	    if (file.getName().equals(name)) {
		session.close();
		return file;
	    }
	}

	final SQLQuery directoryQuery = session
		.createSQLQuery(
			"SELECT DISTINCT t4.ID, t4.TYPE, t4.PARENTDIRECTORY_ID FROM "
				+ "UNTYPEDDATA t1, METADATA_MAP t2, ENTITY_VERSIONS t3, ENTITIES t4 "
				+ "where t3.METADATA_ID=t2.METADATA_ID "
				+ "and t1.id=t2.MYMAP_ID and t2.MYMAP_KEY=15 "
				+ "and t1.STRING=:name and t3.PRIMARYENTITYID=t4.ID "
				+ "and t4.TYPE='D' and t4.PARENTDIRECTORY_ID=:parent")
		.addEntity(PrimaryDataDirectoryImplementation.class);

	directoryQuery.setString("name", name);
	directoryQuery.setString("parent", this.getID());

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<PrimaryDataDirectoryImplementation> dirs = directoryQuery
		.list();

	for (final PrimaryDataDirectoryImplementation dir : dirs) {
	    if (dir.getName().equals(name)) {
		session.close();
		return dir;
	    }
	}
	session.close();
	return null;
    }

    /**
     * Check if the parent {@link PrimaryDataDirectory} is a parent directory of
     * the child {@link PrimaryDataEntity}
     * 
     * @param parent
     * @param child
     * @return true if the parent is a parentDirectory of the child object.
     */
    private boolean checkIfParentEntity(final PrimaryDataDirectory parent,
	    final PrimaryDataEntity child) {

	if (parent.getID().equals(child.getID())) {
	    return true;
	} else {
	    PrimaryDataDirectory grandParent = null;
	    try {
		grandParent = child.getParentDirectory();
	    } catch (final PrimaryDataDirectoryException e) {
		e.printStackTrace();
	    }

	    if (grandParent == null) {
		return false;
	    }
	    return this.checkIfParentEntity(parent, grandParent);
	}
    }

    /**
     * Check if the query string consists any special character of the LUCENE
     * query parser syntax.
     * 
     * @param query
     * @return <em>true</em>
     */
    private boolean consistsQueryParserSyntax(final String query) {
	if (query.contains("+") || query.contains("*") || query.contains("?")
		|| query.contains("~") || query.contains(":")
		|| query.contains("{") || query.contains("}")
		|| query.contains("[") || query.contains("]")
		|| query.contains("^") || query.contains("-")
		|| query.contains(" not ") || query.contains(" or ")
		|| query.contains(" and ") || query.contains(" OR ")
		|| query.contains(" AND ") || query.contains(" NOT ")) {
	    return true;
	}
	return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean existImpl(final String path)
	    throws PrimaryDataDirectoryException {

	try {
	    if (this.checkIfEntityExists(path) == null) {
		return false;
	    }

	} catch (final Exception e) {
	    throw new PrimaryDataDirectoryException(
		    "unable to check if the Entity exist", e);
	}
	return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em> HIBERNATE : constant length cause it is an
     * {@link java.util.UUID}</em>
     */
    @Override
    @Id
    @Column(columnDefinition = "char(40)")
    public String getID() {
	return super.getID();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em> HIBERNATE : FetchType.EAGER for getPath()</em>
     */
    @Override
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    public PrimaryDataDirectoryImplementation getParentDirectory() {
	try {
	    return (PrimaryDataDirectoryImplementation) super.getParentDirectory();
	} catch (final PrimaryDataDirectoryException e) {
	    return null;
	}
    }

    /** {@inheritDoc} */
    @Override
    @Transient
    protected Map<Principal, List<EdalPermission>> getPermissionsImpl()
	    throws PrimaryDataEntityException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Criteria query = session
		.createCriteria(EdalPermissionImplementation.class)
		.add(Restrictions.eq("internId", this.getID()))
		.add(Restrictions.eq("internVersion",
			this.getCurrentVersion().getRevision()));

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<EdalPermissionImplementation> privatePerms = query.list();

	final Map<Principal, List<EdalPermission>> publicMap = new HashMap<>();

	try {
	    for (final EdalPermissionImplementation p : privatePerms) {

		if (!publicMap.containsKey(p.getPrincipal().toPrincipal())) {

		    final Criteria tmpQuery = session
			    .createCriteria(EdalPermissionImplementation.class)
			    .add(Restrictions.eq("internId", this.getID()))
			    .add(Restrictions.eq("internVersion",
				    this.getCurrentVersion().getRevision()))
			    .add(Restrictions.eq("principal",
				    p.getPrincipal()));

		    @SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		    final List<EdalPermissionImplementation> userPerms = tmpQuery
			    .list();
		    final List<EdalPermission> publicPerms = new ArrayList<EdalPermission>(
			    privatePerms.size());

		    for (final EdalPermissionImplementation permission : userPerms) {
			publicPerms.add(permission.toEdalPermission());
		    }
		    publicMap.put(p.getPrincipal().toPrincipal(), publicPerms);
		}
	    }
	} catch (final Exception e) {
	    session.close();
	    throw new PrimaryDataEntityException("Unable to load permissions !",
		    e);
	}
	session.close();

	return publicMap;
    }

    /** {@inheritDoc} */
    @Override
    protected PrimaryDataEntity getPrimaryDataEntityImpl(final String name)
	    throws PrimaryDataDirectoryException {
	return this.checkIfEntityExists(name);
    }

    /**
     * Getter for the field <code>versionList</code>.
     * 
     * @return a {@link SortedSet} object.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "primaryEntityId")
    @SortNatural
    protected SortedSet<PrimaryDataEntityVersionImplementation> getVersionList() {
	return this.versionList;
    }

    /** {@inheritDoc} */
    @Override
    @Transient
    protected SortedSet<PrimaryDataEntityVersion> getVersionsImpl() {

	if (this.getVersionList() == null) {
	    return Collections.synchronizedSortedSet(
		    new TreeSet<PrimaryDataEntityVersion>());
	} else {
	    return Collections.synchronizedSortedSet(
		    new TreeSet<PrimaryDataEntityVersion>(
			    this.getVersionList()));
	}

    }

    /** {@inheritDoc} */
    @Override
    protected List<PrimaryDataEntity> listPrimaryDataEntitiesImpl(
	    final Calendar currentVersionDate, final Calendar nextVersionDate)
	    throws PrimaryDataDirectoryException {

	final ListThread thread = new ListThread(this, currentVersionDate,
		nextVersionDate);

	DataManager.getListThreadPool().execute(thread);

	return thread.getAsynchronList();

    }

    /** {@inheritDoc} */
    @Override
    protected void moveImpl(final PrimaryDataDirectory destinationDirectory) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();
	final Transaction transaction = session.beginTransaction();

	this.setParentDirectory(destinationDirectory);

	session.update(this);
	transaction.commit();
	session.close();
    }

    /**
     * Internal function to search for a {@link DataFormat}.
     * 
     * @param dataFormat
     * @return List<MyDataFormat>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyDataFormat> searchByDataFormat(final DataFormat dataFormat,
	    final boolean fuzzy) throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.Query query = null;

	if (fuzzy) {
	    final QueryParser queryParser = new QueryParser("mimeType",
		    new StandardAnalyzer());
	    queryParser.setAllowLeadingWildcard(true);

	    if (this.consistsQueryParserSyntax(dataFormat.getMimeType())) {
		query = queryParser.parse(dataFormat.getMimeType());
	    } else {
		query = queryParser.parse("*" + dataFormat.getMimeType() + "*");
	    }
	    /**
	     * query = new FuzzyQuery(new
	     * Term("_mimeType",dataFormat.get_mimeType()));
	     */
	} else {
	    query = new TermQuery(
		    new Term("mimeType", dataFormat.getMimeType()));
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final org.hibernate.Query hibernateQuery = ftSession
		.createFullTextQuery(query, MyDataFormat.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyDataFormat> result = hibernateQuery.list();

	session.close();
	return result;
    }

    /**
     * Internal function to search for a {@link DataType}.
     * <p>
     * No fuzzy search possible, because it is an EnumValue
     * 
     * @param dataType
     * @return List<MyDataType>
     */
    private List<MyDataType> searchByDataType(final DataType dataType) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final org.apache.lucene.search.Query query = new TermQuery(new Term(
		"string", dataType.getDataType().toString().toLowerCase()));

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final org.hibernate.Query hibernateQuery = ftSession
		.createFullTextQuery(query, MyDataType.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyDataType> result = hibernateQuery.list();

	session.close();
	return result;
    }

    /**
     * Internal function to search for a {@link DateEvents}.
     * 
     * @param dateEvents
     * @return List<MyDateEvents>
     */
    private List<MyDateEvents> searchByDateEvents(final DateEvents dateEvents) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final List<MyDateEvents> result = new ArrayList<MyDateEvents>();

	final Set<EdalDate> set = dateEvents.getSet();

	if (set.size() > 1) {
	    ((FileSystemImplementationProvider) DataManager.getImplProv())
		    .getLogger()
		    .warn("no DateEvents with multiple BasicDates allowed");
	}

	else if (set.size() == 1) {

	    for (final EdalDate edalDate : set) {

		if (edalDate instanceof EdalDateRange) {
		    final List<MyEdalDateRange> list = this
			    .searchByEDALDateRange((EdalDateRange) edalDate);
		    final Query metaDataQuery = session.createSQLQuery(
			    "select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

		    metaDataQuery.setParameterList("list", list);
		    @SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		    final List<Integer> idlist = metaDataQuery.list();

		    for (final Integer integer : idlist) {
			result.add(session.get(MyDateEvents.class, integer));
		    }
		}

		else if (edalDate instanceof EdalDate) {

		    final List<MyEdalDate> list = this
			    .searchByEDALDate(edalDate);

		    final Query metaDataQuery = session.createSQLQuery(
			    "select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

		    metaDataQuery.setParameterList("list", list);
		    @SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		    final List<Integer> idlist = metaDataQuery.list();

		    for (final Integer integer : idlist) {
			result.add(session.get(MyDateEvents.class, integer));
		    }
		}

	    }

	}

	session.close();

	return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
    protected List<PrimaryDataEntity> searchByDublinCoreElementImpl(
	    final EnumDublinCoreElements element, final UntypedData data,
	    final boolean fuzzy, final boolean recursiveIntoSubdirectories)
	    throws PrimaryDataDirectoryException {

	final long startTime = System.currentTimeMillis();

	List<? extends MyUntypedData> datatypeList = new ArrayList<MyUntypedData>();
	try {
	    if (data.getClass().equals(UntypedData.class)) {
		datatypeList = this.searchByUntypedData(data, fuzzy);

	    } else if (data.getClass().equals(NaturalPerson.class)) {
		datatypeList = this.searchByNaturalPerson((NaturalPerson) data,
			fuzzy);
	    } else if (data.getClass().equals(NaturalPerson.class)) {
		datatypeList = this.searchByLegalPerson((LegalPerson) data,
			fuzzy);
	    } else if (data.getClass().equals(Identifier.class)) {
		datatypeList = this.searchByIdentifier((Identifier) data,
			fuzzy);

	    } else if (data.getClass().equals(DataType.class)) {
		datatypeList = this.searchByDataType((DataType) data);

	    } else if (data.getClass().equals(DataFormat.class)) {
		datatypeList = this.searchByDataFormat((DataFormat) data,
			fuzzy);

	    } else if (data.getClass().equals(DateEvents.class)) {
		datatypeList = this.searchByDateEvents((DateEvents) data);

	    } else if (data.getClass().equals(IdentifierRelation.class)) {
		datatypeList = this.searchByIdentifierRelation(
			(IdentifierRelation) data, fuzzy);
	    }
	} catch (final ParseException e) {
	    throw new PrimaryDataDirectoryException(
		    "Unable to find the UntypedData values", e);
	}
	((FileSystemImplementationProvider) DataManager.getImplProv())
		.getLogger()
		.debug("Zeit (Search " + data.getClass().getSimpleName() + "): "
			+ (System.currentTimeMillis() - startTime) + " msec");

	/** if no results found return empty List */
	if (datatypeList.isEmpty()) {
	    return new ArrayList<PrimaryDataEntity>();
	}
	if (datatypeList
		.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
	    throw new PrimaryDataDirectoryException(
		    "find to much result please repeat query with more details");
	}

	final List<Integer> datatypeIDList = new ArrayList<Integer>(
		datatypeList.size());

	for (final MyUntypedData myUntypedData : datatypeList) {
	    datatypeIDList.add(myUntypedData.getId());
	}

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Query versionSQLQuery = session
		.createSQLQuery("SELECT DISTINCT v.ID "
			+ "FROM ENTITY_VERSIONS v , metadata_map m , "
			+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_key=:key "
			+ "AND m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

	versionSQLQuery.setParameterList("list", datatypeIDList);
	versionSQLQuery.setParameter("key", element.ordinal());

	final List<Integer> versionIDList = versionSQLQuery.list();

	final HashSet<PrimaryDataEntity> resultSet = new HashSet<PrimaryDataEntity>();

	final long startEntityQuery = System.currentTimeMillis();

	if (!recursiveIntoSubdirectories) {
	    for (final Integer version : versionIDList) {
		final PrimaryDataEntityVersionImplementation currentVersion = session
			.get(PrimaryDataEntityVersionImplementation.class,
				version);

		final Criteria fileCriteria = session
			.createCriteria(PrimaryDataFileImplementation.class)
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_CLASS,
				PrimaryDataFileImplementation.class))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_ID,
				currentVersion.getPrimaryEntityId()))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
				this))
			.setCacheable(false).setCacheRegion(
				PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

		final Criteria directoryCriteria = session
			.createCriteria(
				PrimaryDataDirectoryImplementation.class)
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_CLASS,
				PrimaryDataDirectoryImplementation.class))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_ID,
				currentVersion.getPrimaryEntityId()))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
				this))
			.setCacheable(false).setCacheRegion(
				PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

		final PrimaryDataFile primaryDataFile = (PrimaryDataFile) fileCriteria
			.uniqueResult();

		final PrimaryDataDirectory primaryDataDirectory = (PrimaryDataDirectory) directoryCriteria
			.uniqueResult();

		if (primaryDataFile != null) {
		    try {
			if (!primaryDataFile.getCurrentVersion().isDeleted()) {
			    primaryDataFile
				    .switchCurrentVersion(currentVersion);
			}
		    } catch (final PrimaryDataEntityVersionException e) {
			throw new PrimaryDataDirectoryException(
				PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION,
				e);
		    }
		    resultSet.add(primaryDataFile);
		} else {
		    if (primaryDataDirectory != null) {
			try {
			    if (!primaryDataDirectory.getCurrentVersion()
				    .isDeleted()) {
				primaryDataDirectory
					.switchCurrentVersion(currentVersion);
			    }
			} catch (final PrimaryDataEntityVersionException e) {
			    throw new PrimaryDataDirectoryException(
				    PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION,
				    e);
			}
			resultSet.add(primaryDataDirectory);
		    }
		}
	    }
	} else {
	    /** save all object that are found in the directory */
	    final List<PrimaryDataEntityVersionImplementation> maybeInSubDirectoriesList = new ArrayList<PrimaryDataEntityVersionImplementation>();

	    for (final Integer version : versionIDList) {
		final PrimaryDataEntityVersionImplementation currentVersion = session
			.get(PrimaryDataEntityVersionImplementation.class,
				version);
		try {
		    if (!currentVersion.getMetaData()
			    .getElementValue(EnumDublinCoreElements.TYPE)
			    .toString().equals(MetaData.DIRECTORY.toString())) {

			final Criteria fileQuery = session
				.createCriteria(
					PrimaryDataFileImplementation.class)
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_ID,
					currentVersion.getPrimaryEntityId()))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_CLASS,
					PrimaryDataFileImplementation.class))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
					this))
				.setCacheable(false).setCacheRegion(
					PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

			final PrimaryDataFile pdf = (PrimaryDataFile) fileQuery
				.uniqueResult();

			if (pdf != null) {
			    try {
				if (!pdf.getCurrentVersion().isDeleted()) {
				    pdf.switchCurrentVersion(currentVersion);
				}
			    } catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException(
					"Unable to switch version", e);
			    }
			    resultSet.add(pdf);
			} else {
			    maybeInSubDirectoriesList.add(currentVersion);
			}
		    } else {
			final Criteria directoryQuery = session
				.createCriteria(
					PrimaryDataDirectoryImplementation.class)
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_ID,
					currentVersion.getPrimaryEntityId()))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_CLASS,
					PrimaryDataDirectoryImplementation.class))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
					this))
				.setCacheable(false).setCacheRegion(
					PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

			final PrimaryDataDirectory pdd = (PrimaryDataDirectory) directoryQuery
				.uniqueResult();
			if (pdd != null) {
			    try {
				if (!pdd.getCurrentVersion().isDeleted()) {
				    pdd.switchCurrentVersion(currentVersion);
				}
			    } catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException(
					"Unable to switch version", e);
			    }
			    resultSet.add(pdd);
			} else {
			    maybeInSubDirectoriesList.add(currentVersion);
			}
		    }
		} catch (final MetaDataException e) {
		    throw new PrimaryDataDirectoryException(
			    "Unable to check object type", e);
		}
	    }
	    session.close();

	    for (final PrimaryDataEntityVersionImplementation version : maybeInSubDirectoriesList) {
		final PrimaryDataEntity entity = this
			.searchIntoSubdirectories(this, version);
		if (entity != null) {
		    if (entity.isDirectory()) {
			/**
			 * prevent that object itself will be add to the list
			 */
			if (!((PrimaryDataDirectory) entity).getID()
				.equals(this.getID())) {
			    resultSet.add(entity);
			}
		    } else {
			resultSet.add(entity);
		    }
		}
	    }
	}

	((FileSystemImplementationProvider) DataManager.getImplProv())
		.getLogger()
		.debug("Zeit (Search Entity)    : "
			+ (System.currentTimeMillis() - startEntityQuery)
			+ " msec");

	final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(
		resultSet);

	((FileSystemImplementationProvider) DataManager.getImplProv())
		.getLogger().info("Zeit (Search by Element): "
			+ (System.currentTimeMillis() - startTime) + " msec");
	session.close();
	return results;

    }

    /**
     * Internal function to search for a {@link EdalDate}.
     * 
     * @param edalDate
     * @return List<MyEDALDate>
     */
    private List<MyEdalDate> searchByEDALDate(final EdalDate edalDate) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Criteria myEDALDateCriteria = session
		.createCriteria(MyEdalDate.class);

	final int precission = edalDate.getStartPrecision().ordinal();
	final Calendar date = edalDate.getStartDate();

	if (precission == EdalDatePrecision.CENTURY.ordinal()) {
	    ((FileSystemImplementationProvider) DataManager.getImplProv())
		    .getLogger()
		    .warn("no Dates with CENTURY Precission allowed");
	    return new ArrayList<MyEdalDate>();

	} else if (precission >= EdalDatePrecision.DECADE.ordinal()) {

	    /** note: use DECADE(date) if the database-SQL support */
	    myEDALDateCriteria.add(
		    Restrictions.sqlRestriction("SUBSTR(YEAR(startDate),1,3)="
			    + Integer.toString(date.get(Calendar.YEAR))
				    .substring(0, 3)));

	    if (precission >= EdalDatePrecision.YEAR.ordinal()) {
		myEDALDateCriteria.add(Restrictions.sqlRestriction(
			"YEAR(startDate)=" + date.get(Calendar.YEAR)));

		if (precission >= EdalDatePrecision.MONTH.ordinal()) {
		    /** very important: Calendar count months from 0-11 */
		    myEDALDateCriteria
			    .add(Restrictions.sqlRestriction("MONTH(startDate)="
				    + (date.get(Calendar.MONTH) + 1)));

		    if (precission >= EdalDatePrecision.DAY.ordinal()) {
			myEDALDateCriteria.add(
				Restrictions.sqlRestriction("DAY(startDate)="
					+ date.get(Calendar.DAY_OF_MONTH)));
			if (precission >= EdalDatePrecision.HOUR.ordinal()) {
			    myEDALDateCriteria.add(Restrictions
				    .sqlRestriction("HOUR(startDate)="
					    + date.get(Calendar.HOUR_OF_DAY)));
			    if (precission >= EdalDatePrecision.MINUTE
				    .ordinal()) {
				myEDALDateCriteria.add(Restrictions
					.sqlRestriction("MINUTE(startDate)="
						+ date.get(Calendar.MINUTE)));
				if (precission >= EdalDatePrecision.SECOND
					.ordinal()) {
				    myEDALDateCriteria.add(Restrictions
					    .sqlRestriction("SECOND(startDate)="
						    + date.get(
							    Calendar.SECOND)));
				    if (precission >= EdalDatePrecision.MILLISECOND
					    .ordinal()) {
					myEDALDateCriteria.add(
						Restrictions.sqlRestriction(
							"MILLISECOND(startDate)="
								+ date.get(
									Calendar.MILLISECOND)));
				    }
				}
			    }
			}
		    }
		}
	    }
	}

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyEdalDate> result = myEDALDateCriteria.list();
	session.close();

	return result;

    }

    /**
     * Internal function to search for a {@link EdalDateRange}.
     * 
     * @param edalDateRange
     * @return List<MyEDALDateRange>
     */
    private List<MyEdalDateRange> searchByEDALDateRange(
	    final EdalDateRange edalDateRange) {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Criteria myEDALDateCriteria = session
		.createCriteria(MyEdalDateRange.class);

	final int precissionStart = edalDateRange.getStartPrecision().ordinal();
	final Calendar dateStart = edalDateRange.getStartDate();

	final int precissionEnd = edalDateRange.getEndPrecision().ordinal();
	final Calendar dateEnd = edalDateRange.getEndDate();

	if (precissionStart == EdalDatePrecision.CENTURY.ordinal()
		|| precissionEnd == EdalDatePrecision.CENTURY.ordinal()) {
	    ((FileSystemImplementationProvider) DataManager.getImplProv())
		    .getLogger()
		    .warn("no DateRanges with CENTURY Precission allowed");
	    return new ArrayList<MyEdalDateRange>();

	}

	if (precissionStart >= EdalDatePrecision.DECADE.ordinal()) {

	    /** note: use DECADE(date) if the database-SQL support */
	    myEDALDateCriteria.add(
		    Restrictions.sqlRestriction("SUBSTR(YEAR(startDate),1,3)="
			    + Integer.toString(dateStart.get(Calendar.YEAR))
				    .substring(0, 3)));

	    if (precissionStart >= EdalDatePrecision.YEAR.ordinal()) {
		myEDALDateCriteria.add(Restrictions.sqlRestriction(
			"YEAR(startDate)=" + dateStart.get(Calendar.YEAR)));

		if (precissionStart >= EdalDatePrecision.MONTH.ordinal()) {
		    /** very important: Calendar count months from 0-11 */
		    myEDALDateCriteria
			    .add(Restrictions.sqlRestriction("MONTH(startDate)="
				    + (dateStart.get(Calendar.MONTH) + 1)));

		    if (precissionStart >= EdalDatePrecision.DAY.ordinal()) {
			myEDALDateCriteria.add(Restrictions
				.sqlRestriction("DAY(startDate)=" + dateStart
					.get(Calendar.DAY_OF_MONTH)));
			if (precissionStart >= EdalDatePrecision.HOUR
				.ordinal()) {
			    myEDALDateCriteria.add(Restrictions.sqlRestriction(
				    "HOUR(startDate)=" + dateStart
					    .get(Calendar.HOUR_OF_DAY)));
			    if (precissionStart >= EdalDatePrecision.MINUTE
				    .ordinal()) {
				myEDALDateCriteria
					.add(Restrictions.sqlRestriction(
						"MINUTE(startDate)=" + dateStart
							.get(Calendar.MINUTE)));
				if (precissionStart >= EdalDatePrecision.SECOND
					.ordinal()) {
				    myEDALDateCriteria.add(Restrictions
					    .sqlRestriction("SECOND(startDate)="
						    + dateStart.get(
							    Calendar.SECOND)));
				    if (precissionStart >= EdalDatePrecision.MILLISECOND
					    .ordinal()) {
					myEDALDateCriteria.add(
						Restrictions.sqlRestriction(
							"MILLISECOND(startDate)="
								+ dateStart.get(
									Calendar.MILLISECOND)));
				    }
				}
			    }
			}
		    }
		}
	    }
	}

	if (precissionEnd >= EdalDatePrecision.DECADE.ordinal()) {
	    /** note: use DECADE(endDate) if the database-SQL support */
	    myEDALDateCriteria.add(
		    Restrictions.sqlRestriction("SUBSTR(YEAR(endDate),1,3)="
			    + Integer.toString(dateEnd.get(Calendar.YEAR))
				    .substring(0, 3)));

	    if (precissionEnd >= EdalDatePrecision.YEAR.ordinal()) {
		myEDALDateCriteria.add(Restrictions.sqlRestriction(
			"YEAR(endDate)=" + dateEnd.get(Calendar.YEAR)));

		if (precissionEnd >= EdalDatePrecision.MONTH.ordinal()) {
		    /** very important: Calendar count months from 0-11 */
		    myEDALDateCriteria
			    .add(Restrictions.sqlRestriction("MONTH(endDate)="
				    + (dateEnd.get(Calendar.MONTH) + 1)));

		    if (precissionEnd >= EdalDatePrecision.DAY.ordinal()) {
			myEDALDateCriteria
				.add(Restrictions.sqlRestriction("DAY(endDate)="
					+ dateEnd.get(Calendar.DAY_OF_MONTH)));
			if (precissionEnd >= EdalDatePrecision.HOUR.ordinal()) {
			    myEDALDateCriteria.add(Restrictions
				    .sqlRestriction("HOUR(endDate)=" + dateEnd
					    .get(Calendar.HOUR_OF_DAY)));
			    if (precissionEnd >= EdalDatePrecision.MINUTE
				    .ordinal()) {
				myEDALDateCriteria
					.add(Restrictions.sqlRestriction(
						"MINUTE(endDate)=" + dateEnd
							.get(Calendar.MINUTE)));
				if (precissionEnd >= EdalDatePrecision.SECOND
					.ordinal()) {
				    myEDALDateCriteria.add(Restrictions
					    .sqlRestriction("SECOND(endDate)="
						    + dateEnd.get(
							    Calendar.SECOND)));
				    if (precissionEnd >= EdalDatePrecision.MILLISECOND
					    .ordinal()) {
					myEDALDateCriteria.add(
						Restrictions.sqlRestriction(
							"MILLISECOND(endDate)="
								+ dateEnd.get(
									Calendar.MILLISECOND)));
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyEdalDateRange> result = myEDALDateCriteria.list();

	session.close();

	return result;
    }

    /**
     * Internal function to search for a {@link Identifier}.
     * 
     * @param identifier
     * @param fuzzy
     * @return List<MyIdentifier>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyIdentifier> searchByIdentifier(final Identifier identifier,
	    final boolean fuzzy) throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.Query query = null;

	if (fuzzy) {
	    final QueryParser queryParser = new QueryParser("identifier",
		    new StandardAnalyzer());
	    queryParser.setAllowLeadingWildcard(true);

	    if (this.consistsQueryParserSyntax(identifier.getID())) {
		query = queryParser.parse(identifier.getID());
	    } else {
		query = queryParser.parse("*" + identifier.getID() + "*");
	    }

	    /**
	     * query = new FuzzyQuery(new
	     * Term("identifier",identifier.getID()));
	     */
	} else {
	    query = new TermQuery(new Term("identifier", identifier.getID()));
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final org.hibernate.Query hibernateQuery = ftSession
		.createFullTextQuery(query, MyIdentifier.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyIdentifier> result = hibernateQuery.list();

	session.close();
	return result;
    }

    /**
     * Internal function to search for a {@link IdentifierRelation}.
     * 
     * @param identifierRelation
     * @param fuzzy
     * @return List<MyIdentifierRelation>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyIdentifierRelation> searchByIdentifierRelation(
	    final IdentifierRelation identifierRelation, final boolean fuzzy)
	    throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final List<MyIdentifierRelation> result = new ArrayList<MyIdentifierRelation>();

	if (identifierRelation.getRelations().size() > 1) {
	    ((FileSystemImplementationProvider) DataManager.getImplProv())
		    .getLogger()
		    .warn("only IdentifierRelations with only one Identifier allowed");
	}

	else if (identifierRelation.getRelations().size() == 1) {

	    Identifier id = null;
	    for (final Identifier identifier : identifierRelation) {
		id = identifier;
	    }

	    final List<MyIdentifier> myIdentifierList = this
		    .searchByIdentifier(id, fuzzy);

	    if (!myIdentifierList.isEmpty()) {
		final Query metaDataQuery = session.createQuery(
			"select D.id from MyIdentifierRelation D join D.relations V where V in (:list)");

		metaDataQuery.setParameterList("list", myIdentifierList);

		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		final List<Integer> idlist = metaDataQuery.list();

		for (final Integer integer : idlist) {
		    result.add(
			    session.get(MyIdentifierRelation.class, integer));

		}
	    }

	}
	return result;

    }

    @Override
    protected List<? extends PrimaryDataEntity> searchByKeywordImpl(
	    final String keyword, final boolean fuzzy,
	    final boolean recursiveIntoSubdirectories)
	    throws PrimaryDataDirectoryException {

	final long startTime = System.currentTimeMillis();

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.Query query = null;

	final MultiFieldQueryParser parser = new MultiFieldQueryParser(
		new String[] { "string", "givenName", "sureName", "country",
			"zip", "addressLine", "id", "identifier", "mimeType" },
		new StandardAnalyzer());

	parser.setAllowLeadingWildcard(true);

	try {
	    if (fuzzy) {
		if (this.consistsQueryParserSyntax(keyword)) {
		    query = parser.parse(keyword);
		} else {
		    query = parser.parse("*" + keyword + "*");
		}

	    } else {
		query = parser.parse(keyword);
	    }
	} catch (final ParseException e) {
	    throw new PrimaryDataDirectoryException(
		    "Unable to find the UntypedData values", e);
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final Query hibernateQuery = ftSession.createFullTextQuery(query,
		UntypedData.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyUntypedData> datatypeList = hibernateQuery.list();

	session.close();

	/** if no results found return empty List */
	if (datatypeList.isEmpty()) {
	    return new ArrayList<PrimaryDataEntity>();
	}
	if (datatypeList
		.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
	    throw new PrimaryDataDirectoryException(
		    "find to much result please repeat query with more details");
	}

	final List<Integer> datatypeIDList = new ArrayList<Integer>(
		datatypeList.size());

	for (final MyUntypedData myUntypedData : datatypeList) {
	    datatypeIDList.add(myUntypedData.getId());
	}

	final Session session2 = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Query versionSQLQuery = session2
		.createSQLQuery("SELECT DISTINCT v.ID "
			+ "FROM ENTITY_VERSIONS v , metadata_map m , "
			+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

	versionSQLQuery.setParameterList("list", datatypeIDList);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<Integer> versionIDList = versionSQLQuery.list();

	final HashSet<PrimaryDataEntity> resultSet = new HashSet<PrimaryDataEntity>();

	final long startEntityQuery = System.currentTimeMillis();

	if (!recursiveIntoSubdirectories) {
	    for (final Integer version : versionIDList) {
		final PrimaryDataEntityVersionImplementation currentVersion = session2
			.get(PrimaryDataEntityVersionImplementation.class,
				version);

		final Criteria fileCriteria = session2
			.createCriteria(PrimaryDataFileImplementation.class)
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_CLASS,
				PrimaryDataFileImplementation.class))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_ID,
				currentVersion.getPrimaryEntityId()))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
				this))
			.setCacheable(false).setCacheRegion(
				PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

		final Criteria directoryCriteria = session2
			.createCriteria(
				PrimaryDataDirectoryImplementation.class)
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_CLASS,
				PrimaryDataDirectoryImplementation.class))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_ID,
				currentVersion.getPrimaryEntityId()))
			.add(Restrictions.eq(
				PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
				this))
			.setCacheable(false).setCacheRegion(
				PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

		final PrimaryDataFile primaryDataFile = (PrimaryDataFile) fileCriteria
			.uniqueResult();

		final PrimaryDataDirectory primaryDataDirectory = (PrimaryDataDirectory) directoryCriteria
			.uniqueResult();

		if (primaryDataFile != null) {
		    try {
			if (!primaryDataFile.getCurrentVersion().isDeleted()) {
			    primaryDataFile
				    .switchCurrentVersion(currentVersion);
			}
		    } catch (final PrimaryDataEntityVersionException e) {
			throw new PrimaryDataDirectoryException(
				PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION,
				e);
		    }
		    resultSet.add(primaryDataFile);
		} else {
		    if (primaryDataDirectory != null) {
			try {
			    if (!primaryDataDirectory.getCurrentVersion()
				    .isDeleted()) {
				primaryDataDirectory
					.switchCurrentVersion(currentVersion);
			    }
			} catch (final PrimaryDataEntityVersionException e) {
			    throw new PrimaryDataDirectoryException(
				    PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION,
				    e);
			}
			resultSet.add(primaryDataDirectory);
		    }
		}
	    }
	} else {
	    /** save all object that are found in the directory */
	    final List<PrimaryDataEntityVersionImplementation> maybeInSubDirectoriesList = new ArrayList<PrimaryDataEntityVersionImplementation>();

	    for (final Integer version : versionIDList) {
		final PrimaryDataEntityVersionImplementation currentVersion = session2
			.get(PrimaryDataEntityVersionImplementation.class,
				version);
		try {
		    if (!currentVersion.getMetaData()
			    .getElementValue(EnumDublinCoreElements.TYPE)
			    .toString().equals(MetaData.DIRECTORY.toString())) {

			final Criteria fileQuery = session2
				.createCriteria(
					PrimaryDataFileImplementation.class)
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_ID,
					currentVersion.getPrimaryEntityId()))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_CLASS,
					PrimaryDataFileImplementation.class))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
					this))
				.setCacheable(false).setCacheRegion(
					PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

			final PrimaryDataFile pdf = (PrimaryDataFile) fileQuery
				.uniqueResult();

			if (pdf != null) {
			    try {
				if (!pdf.getCurrentVersion().isDeleted()) {
				    pdf.switchCurrentVersion(currentVersion);
				}
			    } catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException(
					"Unable to switch version", e);
			    }
			    resultSet.add(pdf);
			} else {
			    maybeInSubDirectoriesList.add(currentVersion);
			}
		    } else {
			final Criteria directoryQuery = session2
				.createCriteria(
					PrimaryDataDirectoryImplementation.class)
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_ID,
					currentVersion.getPrimaryEntityId()))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_CLASS,
					PrimaryDataDirectoryImplementation.class))
				.add(Restrictions.eq(
					PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY,
					this))
				.setCacheable(false).setCacheRegion(
					PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

			final PrimaryDataDirectory pdd = (PrimaryDataDirectory) directoryQuery
				.uniqueResult();
			if (pdd != null) {
			    try {
				if (!pdd.getCurrentVersion().isDeleted()) {
				    pdd.switchCurrentVersion(currentVersion);
				}
			    } catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException(
					"Unable to switch version", e);
			    }
			    resultSet.add(pdd);
			} else {
			    maybeInSubDirectoriesList.add(currentVersion);
			}
		    }
		} catch (final MetaDataException e) {
		    throw new PrimaryDataDirectoryException(
			    "Unable to check object type", e);
		}
	    }
	    session2.close();

	    for (final PrimaryDataEntityVersionImplementation version : maybeInSubDirectoriesList) {
		final PrimaryDataEntity entity = this
			.searchIntoSubdirectories(this, version);
		if (entity != null) {
		    if (entity.isDirectory()) {
			/**
			 * prevent that object itself will be add to the list
			 */
			if (!((PrimaryDataDirectory) entity).getID()
				.equals(this.getID())) {
			    resultSet.add(entity);
			}
		    } else {
			resultSet.add(entity);
		    }
		}
	    }
	}

	((FileSystemImplementationProvider) DataManager.getImplProv())
		.getLogger()
		.debug("Zeit (Search Entity)    : "
			+ (System.currentTimeMillis() - startEntityQuery)
			+ " msec");

	final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(
		resultSet);

	((FileSystemImplementationProvider) DataManager.getImplProv())
		.getLogger().info("Zeit (Search by keyword): "
			+ (System.currentTimeMillis() - startTime) + " msec");
	return results;

    }

    /**
     * Internal function to search for a {@link LegalPerson}.
     * 
     * @param legalPerson
     * @param fuzzy
     * @return List<MyLegalPerson>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyLegalPerson> searchByLegalPerson(
	    final LegalPerson legalPerson, final boolean fuzzy)
	    throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.BooleanQuery query = null;

	if (fuzzy) {
	    /** givenName */
	    final QueryParser queryParser1 = new QueryParser("legalName",
		    new StandardAnalyzer());
	    queryParser1.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query legalName = null;

	    if (this.consistsQueryParserSyntax(legalPerson.getLegalName())) {
		legalName = queryParser1.parse(legalPerson.getLegalName());
	    } else {
		legalName = queryParser1
			.parse("*" + legalPerson.getLegalName() + "*");
	    }
	    /** addressLine */
	    final QueryParser queryParser2 = new QueryParser("addressLine",
		    new StandardAnalyzer());
	    queryParser2.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query addressLine = null;

	    if (this.consistsQueryParserSyntax(legalPerson.getAddressLine())) {
		addressLine = queryParser2.parse(legalPerson.getAddressLine());
	    } else {
		addressLine = queryParser2
			.parse("*" + legalPerson.getAddressLine() + "*");
	    }
	    /** zip */
	    final QueryParser queryParser3 = new QueryParser("zip",
		    new StandardAnalyzer());
	    queryParser3.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query zip = null;

	    if (this.consistsQueryParserSyntax(legalPerson.getZip())) {
		zip = queryParser3.parse(legalPerson.getZip());
	    } else {
		zip = queryParser3.parse("*" + legalPerson.getZip() + "*");
	    }
	    /** country */
	    final QueryParser queryParser4 = new QueryParser("country",
		    new StandardAnalyzer());
	    queryParser4.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query country = null;

	    if (this.consistsQueryParserSyntax(legalPerson.getCountry())) {
		country = queryParser4.parse(legalPerson.getCountry());
	    } else {
		country = queryParser4
			.parse("*" + legalPerson.getCountry() + "*");
	    }

	    query = new BooleanQuery.Builder()
		    .add(legalName, BooleanClause.Occur.SHOULD)
		    .add(addressLine, BooleanClause.Occur.SHOULD)
		    .add(zip, BooleanClause.Occur.SHOULD)
		    .add(country, BooleanClause.Occur.SHOULD).build();

	} else {

	    final TermQuery legalName = new TermQuery(
		    new Term("legalName", legalPerson.getLegalName()));
	    final TermQuery addressLine = new TermQuery(
		    new Term("addressLine", legalPerson.getAddressLine()));
	    final TermQuery zip = new TermQuery(
		    new Term("zip", legalPerson.getZip()));
	    final TermQuery country = new TermQuery(
		    new Term("country", legalPerson.getCountry()));

	    query = new BooleanQuery.Builder()
		    .add(legalName, BooleanClause.Occur.SHOULD)
		    .add(addressLine, BooleanClause.Occur.SHOULD)
		    .add(zip, BooleanClause.Occur.SHOULD)
		    .add(country, BooleanClause.Occur.SHOULD).build();
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final Query hibernateQuery = ftSession.createFullTextQuery(query,
		MyLegalPerson.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyLegalPerson> result = hibernateQuery.list();

	session.close();
	return result;

    }

    /** {@inheritDoc} */
    @Override
    protected List<PrimaryDataEntity> searchByMetaDataImpl(final MetaData query,
	    final boolean fuzzy, final boolean recursiveIntoSubdirectories)
	    throws PrimaryDataDirectoryException, MetaDataException {

	final HashSet<PrimaryDataEntity> hashSet = new HashSet<PrimaryDataEntity>();

	for (final EnumDublinCoreElements element : EnumDublinCoreElements
		.values()) {
	    final List<PrimaryDataEntity> tempList = this
		    .searchByDublinCoreElement(element,
			    query.getElementValue(element), fuzzy,
			    recursiveIntoSubdirectories);
	    hashSet.addAll(tempList);
	}

	if (hashSet
		.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
	    throw new PrimaryDataDirectoryException(
		    "find to much result please repeat query with more details");
	}
	final List<PrimaryDataEntity> entityList = new ArrayList<PrimaryDataEntity>(
		hashSet);

	return entityList;
    }

    /**
     * Internal function to search for a {@link NaturalPerson}.
     * 
     * @param naturalPerson
     * @param fuzzy
     * @return List<MyNaturalPerson>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyNaturalPerson> searchByNaturalPerson(
	    final NaturalPerson naturalPerson, final boolean fuzzy)
	    throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.BooleanQuery query = null;

	if (fuzzy) {
	    /** givenName */
	    final QueryParser queryParser1 = new QueryParser("givenName",
		    new StandardAnalyzer());
	    queryParser1.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query givenName = null;

	    if (this.consistsQueryParserSyntax(naturalPerson.getGivenName())) {
		givenName = queryParser1.parse(naturalPerson.getGivenName());
	    } else {
		givenName = queryParser1
			.parse("*" + naturalPerson.getGivenName() + "*");
	    }
	    /** sureName */
	    final QueryParser queryParser2 = new QueryParser("sureName",
		    new StandardAnalyzer());
	    queryParser2.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query sureName = null;

	    if (this.consistsQueryParserSyntax(naturalPerson.getGivenName())) {
		sureName = queryParser2.parse(naturalPerson.getSureName());
	    } else {
		sureName = queryParser2
			.parse("*" + naturalPerson.getSureName() + "*");
	    }
	    /** addressLine */
	    final QueryParser queryParser3 = new QueryParser("addressLine",
		    new StandardAnalyzer());
	    queryParser3.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query addressLine = null;

	    if (this.consistsQueryParserSyntax(
		    naturalPerson.getAddressLine())) {
		addressLine = queryParser3
			.parse(naturalPerson.getAddressLine());
	    } else {
		addressLine = queryParser3
			.parse("*" + naturalPerson.getAddressLine() + "*");
	    }
	    /** zip */
	    final QueryParser queryParser4 = new QueryParser("zip",
		    new StandardAnalyzer());
	    queryParser4.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query zip = null;

	    if (this.consistsQueryParserSyntax(naturalPerson.getZip())) {
		zip = queryParser4.parse(naturalPerson.getZip());
	    } else {
		zip = queryParser4.parse("*" + naturalPerson.getZip() + "*");
	    }
	    /** country */
	    final QueryParser queryParser5 = new QueryParser("country",
		    new StandardAnalyzer());
	    queryParser5.setAllowLeadingWildcard(true);

	    org.apache.lucene.search.Query country = null;

	    if (this.consistsQueryParserSyntax(naturalPerson.getCountry())) {
		country = queryParser5.parse(naturalPerson.getCountry());
	    } else {
		country = queryParser5
			.parse("*" + naturalPerson.getCountry() + "*");
	    }

	    query = new BooleanQuery.Builder()
		    .add(givenName, BooleanClause.Occur.SHOULD)
		    .add(sureName, BooleanClause.Occur.SHOULD)
		    .add(addressLine, BooleanClause.Occur.SHOULD)
		    .add(zip, BooleanClause.Occur.SHOULD)
		    .add(country, BooleanClause.Occur.SHOULD).build();
	} else {

	    final TermQuery givenName = new TermQuery(
		    new Term("givenName", naturalPerson.getGivenName()));
	    final TermQuery sureName = new TermQuery(
		    new Term("sureName", naturalPerson.getSureName()));
	    final TermQuery addressLine = new TermQuery(
		    new Term("addressLine", naturalPerson.getAddressLine()));
	    final TermQuery zip = new TermQuery(
		    new Term("zip", naturalPerson.getZip()));
	    final TermQuery country = new TermQuery(
		    new Term("country", naturalPerson.getCountry()));

	    query = new BooleanQuery.Builder()
		    .add(givenName, BooleanClause.Occur.SHOULD)
		    .add(sureName, BooleanClause.Occur.SHOULD)
		    .add(addressLine, BooleanClause.Occur.SHOULD)
		    .add(zip, BooleanClause.Occur.SHOULD)
		    .add(country, BooleanClause.Occur.SHOULD).build();
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final Query hibernateQuery = ftSession.createFullTextQuery(query,
		MyNaturalPerson.class);

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyNaturalPerson> result = hibernateQuery.list();

	session.close();
	return result;

    }

    /** {@inheritDoc} */
    @Override
    protected List<? extends PrimaryDataEntity> searchByPublicationStatusImpl(
	    final PublicationStatus publicationStatus)
	    throws PrimaryDataDirectoryException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final List<PrimaryDataEntity> results = new ArrayList<>();

	final List<PrimaryDataEntityVersionImplementation> maybeResults = new ArrayList<>();

	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<PublicReferenceImplementation> notRequestedList = session
		.createCriteria(PublicReferenceImplementation.class)
		.add(Restrictions.eq("publicationStatus", publicationStatus))
		.setResultTransformer(
			CriteriaSpecification.DISTINCT_ROOT_ENTITY)
		.list();

	for (final PublicReferenceImplementation publicReferenceImplementation : notRequestedList) {
	    final PrimaryDataEntity entity = publicReferenceImplementation
		    .getVersion().getEntity();
	    try {
		if (entity.getParentDirectory().equals(this)) {
		    results.add(entity);
		} else {
		    maybeResults
			    .add(publicReferenceImplementation.getVersion());
		}
	    } catch (final PrimaryDataDirectoryException e) {
		throw new PrimaryDataDirectoryException(
			"unable to search in directory", e);
	    }

	}

	for (final PrimaryDataEntityVersionImplementation version : maybeResults) {
	    try {
		final PrimaryDataEntity entity = this
			.searchIntoSubdirectories(this, version);

		if (entity != null) {
		    results.add(entity);
		}
	    } catch (final PrimaryDataDirectoryException e) {
		throw new PrimaryDataDirectoryException(
			"unable to search into sub directories", e);
	    }

	}

	return results;
    }

    /**
     * Internal function to search for a {@link UntypedData}.
     * 
     * @param data
     * @param fuzzy
     * @return List<MyUntypedData>
     * @throws ParseException
     *             If unable to parse query string with <em>LUCENE<em>.
     */
    private List<MyUntypedData> searchByUntypedData(final UntypedData data,
	    final boolean fuzzy) throws ParseException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	org.apache.lucene.search.Query query = null;

	if (fuzzy) {
	    final QueryParser queryParser = new QueryParser("string",
		    new StandardAnalyzer());
	    queryParser.setAllowLeadingWildcard(true);

	    if (this.consistsQueryParserSyntax(data.getString())) {
		query = queryParser.parse(data.getString());
	    } else {
		query = queryParser.parse("*" + data.getString() + "*");
	    }
	    /**
	     * query = new FuzzyQuery(new Term("_string", data.get_string()));
	     */
	} else {
	    query = new TermQuery(new Term("string", data.getString()));
	}

	final FullTextSession ftSession = Search.getFullTextSession(session);

	final Query hibernateQuery = ftSession.createFullTextQuery(query,
		MyUntypedData.class);
	@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
	final List<MyUntypedData> result = hibernateQuery.list();

	session.close();

	return result;
    }

    /**
     * Search for the version in the sub directories of the current directory
     * 
     * @param entity
     * @param version
     * @return the founded object
     * @throws PrimaryDataDirectoryException
     */
    private PrimaryDataEntity searchIntoSubdirectories(
	    final PrimaryDataDirectory entity,
	    final PrimaryDataEntityVersionImplementation version)
	    throws PrimaryDataDirectoryException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	final Criteria directoryCriteria = session
		.createCriteria(PrimaryDataDirectoryImplementation.class)
		.add(Restrictions.eq(
			PrimaryDataDirectoryImplementation.STRING_ID,
			version.getPrimaryEntityId()))
		.add(Restrictions.eq(
			PrimaryDataDirectoryImplementation.STRING_CLASS,
			PrimaryDataDirectoryImplementation.class))
		.setCacheable(true).setCacheRegion(
			PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

	final PrimaryDataDirectory directory = (PrimaryDataDirectory) directoryCriteria
		.uniqueResult();

	if (directory != null && this.checkIfParentEntity(entity, directory)) {
	    try {
		/**
		 * no switchCurrentVersion if the object is marked as deleted
		 */
		if (!directory.getCurrentVersion().isDeleted()) {
		    directory.switchCurrentVersion(version);
		}
	    } catch (final PrimaryDataEntityVersionException e) {
		throw new PrimaryDataDirectoryException(
			"Unable to switch version", e);
	    }
	    session.close();
	    return directory;
	}

	final Criteria fileCriteria = session
		.createCriteria(PrimaryDataFileImplementation.class)
		.add(Restrictions.eq(
			PrimaryDataDirectoryImplementation.STRING_ID,
			version.getPrimaryEntityId()))
		.add(Restrictions.eq(
			PrimaryDataDirectoryImplementation.STRING_CLASS,
			PrimaryDataFileImplementation.class))
		.setCacheable(true).setCacheRegion(
			PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY);

	final PrimaryDataFile file = (PrimaryDataFile) fileCriteria
		.uniqueResult();

	if (file != null && this.checkIfParentEntity(entity, file)) {
	    try {
		/**
		 * no switchCurrentVersion if the object is marked as deleted
		 */
		if (!file.getCurrentVersion().isDeleted()) {
		    file.switchCurrentVersion(version);
		}
	    } catch (final PrimaryDataEntityVersionException e) {
		throw new PrimaryDataDirectoryException(
			"Unable to switch version", e);
	    }
	    session.close();
	    return file;
	}
	session.close();
	return null;
    }

    /**
     * Setter for the field <code>versionList</code>.
     * 
     * @param versionList
     *            a {@link SortedSet} object.
     */
    protected void setVersionList(
	    final SortedSet<PrimaryDataEntityVersionImplementation> versionList) {

	this.versionList = Collections.synchronizedSortedSet(versionList);
	this.setCurrentVersion(this.versionList.last());
    }

    /** {@inheritDoc} */
    @Override
    protected void storeVersion(final PrimaryDataEntityVersion publicVersion)
	    throws PrimaryDataEntityVersionException {

	final MetaDataImplementation metadata = (MetaDataImplementation) publicVersion
		.getMetaData();

	/** create new version */
	final PrimaryDataEntityVersionImplementation privateVersion = new PrimaryDataEntityVersionImplementation();

	privateVersion.setCreationDate(publicVersion.getCreationDate());
	privateVersion.setPrimaryEntityId(this.getID());
	privateVersion.setMetaData(metadata);
	privateVersion.setRevision(publicVersion.getRevision());
	privateVersion.setDeleted(publicVersion.isDeleted());

	final List<PublicReferenceImplementation> list = new ArrayList<PublicReferenceImplementation>();

	for (final PublicReference publicReference : publicVersion
		.getPublicReferences()) {
	    final PublicReferenceImplementation privateReference = new PublicReferenceImplementation(
		    publicReference);
	    privateReference.setVersion(privateVersion);
	    list.add(privateReference);
	}
	privateVersion.setInternReferences(list);

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();
	final Transaction transaction = session.beginTransaction();

	try {
	    /** saveOrUpdate the finished directory */
	    session.saveOrUpdate(this);
	    /**
	     * saveOrUpdate the version --> Cascade.ALL --> saves automatically
	     * MetaData
	     */
	    session.saveOrUpdate(privateVersion);

	    transaction.commit();
	} catch (final Exception e) {
	    if (transaction != null) {
		transaction.rollback();
	    }
	    session.close();
	    throw new PrimaryDataEntityVersionException(
		    "Unable to store PrimaryDataEntityVersion : "
			    + e.getMessage(),
		    e);
	}

	if (this.versionList == null) {
	    this.versionList = Collections.synchronizedSortedSet(
		    new TreeSet<PrimaryDataEntityVersionImplementation>());
	    this.versionList.add(privateVersion);
	    Collections.synchronizedSortedSet(this.versionList);
	} else {
	    this.versionList.add(privateVersion);
	    Collections.synchronizedSortedSet(this.versionList);
	}

	this.setCurrentVersion(privateVersion);

	try {
	    this.setDefaultPermissions();
	} catch (final PrimaryDataEntityException e) {
	    throw new PrimaryDataEntityVersionException(
		    "Unable to store default permissions : " + e.getMessage(),
		    e);
	}

	for (final Principal principal : DataManager.getSubject()
		.getPrincipals()) {

	    final Transaction transaction2 = session.beginTransaction();

	    final PrincipalImplementation existingPrincipal = (PrincipalImplementation) session
		    .createCriteria(PrincipalImplementation.class)
		    .add(Restrictions.eq("name", principal.getName()))
		    .add(Restrictions.eq("type",
			    principal.getClass().getSimpleName()))
		    .uniqueResult();

	    if (existingPrincipal != null) {
		privateVersion.setOwner(existingPrincipal);
	    } else {
		throw new PrimaryDataEntityVersionException(
			"Unable to load existing Principal");
	    }

	    /** version to add owner */
	    session.saveOrUpdate(privateVersion);
	    transaction2.commit();
	    session.close();
	    break;
	}

	this.setCurrentVersion(privateVersion);

    }
}