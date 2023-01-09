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

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.EdalClasses;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * Implementation of {@link PermissionProvider} interface.
 * 
 * @author arendd
 */
public class PermissionProviderImplementation implements Serializable, PermissionProvider {

	private static final String CACHE_REGION_ROOT = "query.root";
	private static final String CACHE_REGION_FOR_PRINCIPALS = "query.principal";
	private static final String CACHE_REGION_FOR_PERMISSIONS = "query.permission";
	private static final String STRING_INTERN_VERSION = "internVersion";
	private static final String STRING_INTERN_METHOD = "internMethod";
	private static final String STRING_INTERN_CLASS = "internClass";
	private static final String STRING_INTERN_ID = "internId";
	private static final String STRING_PRINCIPAL = "principal";
	private static final String STRING_TYPE = "type";
	private static final String STRING_NAME = "name";

	private static final long serialVersionUID = -8397868034521482885L;
	/**
	 * store the currentEntity ID for database Query in findPermissionsFromDB
	 */

	private static final InheritableThreadLocal<String> THREAD_LOCAL_ENTITY_ID;

	static {
		THREAD_LOCAL_ENTITY_ID = new InheritableThreadLocal<String>();
	}

	/**
	 * Getter for the field <code>THREAD_LOCAL_ENTITY_ID</code>.
	 * 
	 * @return the THREAD_LOCAL_ENTITY_ID
	 */
	public static InheritableThreadLocal<String> getThreadlocalentityid() {
		return THREAD_LOCAL_ENTITY_ID;
	}

	/** {@inheritDoc} */
	@Override
	public List<EdalPermission> findPermissions(final Set<Principal> principalList) {

		DataManager.getImplProv().getLogger().debug("Start FindPermission ");

		final List<EdalPermission> permissions = new ArrayList<EdalPermission>();

		List<EdalPermissionImplementation> internalPermissions = null;

		for (Principal principal : principalList) {
			try {
				internalPermissions = getEDALPermissionsFromDB(principal.getClass().getSimpleName(),
						principal.getName());
				for (EdalPermissionImplementation edalperm : internalPermissions) {
					permissions.add(edalperm.toEdalPermission());
				}
			} finally {
				// break when found one correct principal
				if (!internalPermissions.isEmpty()) {
					break;
				}
			}
		}

		/*
		 * if the user subject consists no granted principal, then check if the
		 * ALLPrincipal is set
		 */
		if (permissions.isEmpty()) {

			internalPermissions = getEDALPermissionsFromDB(ALLPrincipal.class.getSimpleName(),
					new ALLPrincipal().getName());

			for (EdalPermissionImplementation edalperm : internalPermissions) {
				permissions.add(edalperm.toEdalPermission());
			}
		}

		return permissions;
	}

	/**
	 * Query all permission for the current principal
	 * 
	 * @param principalType
	 *            the type of the {@link Principal}
	 * @param principalName
	 *            the name of the {@link Principal}
	 * @return list of all {@link EdalPermission}
	 */
	private List<EdalPermissionImplementation> getEDALPermissionsFromDB(final String principalType,
			final String principalName) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get(STRING_NAME),principalName)),builder.equal(principalRoot.get(STRING_TYPE), principalType));
		
		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PRINCIPALS).uniqueResult();
	
		CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
		Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
		permissionCriteria.where(builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),getThreadlocalentityid().get())),builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal));
		
		List<EdalPermissionImplementation> permissions = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).list();
		
		session.close();

		return permissions;

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Check if the {@link Principal} or the {@link EdalPermission} exists before
	 * grant new permission.
	 */
	@Override
	public void grantPermission(final String principalType, final String principalName,
			final EdalPermission edalPermission) throws PrimaryDataEntityException {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get(STRING_NAME),principalName)),builder.equal(principalRoot.get(STRING_TYPE), principalType));
		
		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PRINCIPALS).uniqueResult();
	
		// check if principal exists
		if (principal == null) {

			principal = new PrincipalImplementation(principalName, principalType);

			EdalPermissionImplementation newPermission = new EdalPermissionImplementation(principal,
					edalPermission.getPrimaryDataEntityID(), edalPermission.getVersion(),
					EdalClasses.valueOf(edalPermission.getActionClass().getSimpleName()),
					Methods.valueOf(edalPermission.getActionMethod().getName()));

			Transaction transaction = session.beginTransaction();
			try {
				// if principal not exists, save new principal
				session.save(principal);
				// save new permission for new principal
				session.save(newPermission);

				transaction.commit();
				session.close();
			} catch (Exception e) {
				if (transaction != null) {
					transaction.rollback();
					throw new PrimaryDataEntityException(
							"Can not save principal for permission: " + e.getMessage() + "-> rollback");
				}
				throw new PrimaryDataEntityException("Can not save principal for permission: " + e.getMessage());
			}
		}

		else {
			// check if permissions exists		
			CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
			Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
			permissionCriteria.where(
					builder.and(
							builder.and(
									builder.and(
											builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
													edalPermission.getPrimaryDataEntityID())),
											builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
									builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
											EdalClasses.valueOf(edalPermission.getActionClass().getSimpleName()))),
							builder.equal(permissionRoot.get(STRING_INTERN_METHOD), Methods.valueOf(edalPermission.getActionMethod().getName()))),builder.equal(permissionRoot.get(STRING_INTERN_VERSION), edalPermission.getVersion()));
			
			EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
		
			if (permission == null) {
				EdalPermissionImplementation newPermission = new EdalPermissionImplementation(principal,
						edalPermission.getPrimaryDataEntityID(), edalPermission.getVersion(),
						EdalClasses.valueOf(edalPermission.getActionClass().getSimpleName()),
						Methods.valueOf(edalPermission.getActionMethod().getName()));

				Transaction transaction2 = session.beginTransaction();
				try {
					// save new permission for new principal
					session.save(newPermission);
					transaction2.commit();
					session.close();
				} catch (Exception e) {
					if (transaction2 != null) {
						transaction2.rollback();
						throw new PrimaryDataEntityException(
								"Can not save permission: " + e.getMessage() + "-> rollback");
					}
					throw new PrimaryDataEntityException("Can not save permission: " + e.getMessage());
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void grantPermission(String principalType, String principalName, PrimaryDataEntity entity)
			throws PrimaryDataEntityException {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get(STRING_NAME),principalName)),builder.equal(principalRoot.get(STRING_TYPE), principalType));

		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PRINCIPALS).uniqueResult();
		
		// check if principal exists
		if (principal == null) {

			principal = new PrincipalImplementation(principalName, principalType);

			Transaction transaction = session.beginTransaction();
			// if principal not exists, save new principal
			session.save(principal);
			transaction.commit();

		}

		/* check if the principal is the ALLPrincipal */

		Boolean isAllPrincipal = false;
		if (principal.getType().equals(ALLPrincipal.class.getSimpleName())
				&& principal.getName().equals(new ALLPrincipal().getName())) {
			isAllPrincipal = true;
		}

		Transaction trans = session.beginTransaction();

		for (final Methods method : GrantableMethods.ENTITY_METHODS) {

			/* if ALLPrincipal, then set no permission for grant/revoke */

			if (!(isAllPrincipal
					&& (method.equals(Methods.grantPermission) || method.equals(Methods.revokePermission)))) {

				CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
				Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
				permissionCriteria.where(
						builder.and(
								builder.and(
										builder.and(
												builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
														entity.getID())),
												builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
										builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
												EdalClasses.PrimaryDataEntity)),
								builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
						builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
				
				EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
				
				// check if permission exists
				if (permission == null) {
					EdalPermissionImplementation newPermission = new EdalPermissionImplementation(principal,
							entity.getID(), entity.getCurrentVersion().getRevision(), EdalClasses.PrimaryDataEntity,
							method);

					// if permission not exists, save new permission
					session.save(newPermission);
				}
			}

		}

		if (entity.isDirectory()) {
			for (final Methods method : GrantableMethods.DIRECTORY_METHODS) {
				
				CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
				Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
				permissionCriteria.where(
						builder.and(
								builder.and(
										builder.and(
												builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
														entity.getID())),
												builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
										builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
												EdalClasses.PrimaryDataDirectory)),
								builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
						builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
				
				EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
				
				// check if permission exists
				if (permission == null) {
					EdalPermissionImplementation newPermission = new EdalPermissionImplementation(principal,
							entity.getID(), entity.getCurrentVersion().getRevision(), EdalClasses.PrimaryDataDirectory,
							method);

					// if permission not exists, save new permission
					session.save(newPermission);
				}
			}
		}

		if (!entity.isDirectory()) {
			for (final Methods method : GrantableMethods.FILE_METHODS) {

				CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
				Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
				permissionCriteria.where(
						builder.and(
								builder.and(
										builder.and(
												builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
														entity.getID())),
												builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
										builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
												EdalClasses.PrimaryDataFile)),
								builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
						builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
				
				EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
	
				// check if permission exists
				if (permission == null) {
					EdalPermissionImplementation newPermission = new EdalPermissionImplementation(principal,
							entity.getID(), entity.getCurrentVersion().getRevision(), EdalClasses.PrimaryDataFile,
							method);

					// if permission not exists, save new permission
					session.save(newPermission);
				}
			}
		}

		trans.commit();

		session.close();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRoot(final Principal principal) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<RootImplementation> rootCriteria = builder.createQuery(RootImplementation.class);
		Root<RootImplementation> rootRoot = rootCriteria.from(RootImplementation.class);

		rootCriteria.select(rootRoot);

		RootImplementation root = session.createQuery(rootCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_ROOT).uniqueResult();

		if (root == null) {
			Transaction transaction = session.beginTransaction();
			session.save(new RootImplementation(principal.getName(), principal.getClass().getSimpleName()));
			transaction.commit();
			session.close();
			return true;
		} else {
			if (root.getName().equals(principal.getName())
					&& root.getType().equals(principal.getClass().getSimpleName())) {
				session.close();
				return true;
			} else {
				session.close();
				return false;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void revokePermission(final String principalType, final String principalName,
			final EdalPermission edalPermission) throws PrimaryDataEntityException {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get(STRING_NAME),principalName)),builder.equal(principalRoot.get(STRING_TYPE), principalType));

		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PRINCIPALS).uniqueResult();
		
		if (principal == null) {
			throw new PrimaryDataEntityException("couldn't found the correct principal to delete permission"
					+ edalPermission.getActionMethod().getName());
		}

		CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
		Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
		permissionCriteria.where(
				builder.and(
						builder.and(
								builder.and(
										builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
												edalPermission.getPrimaryDataEntityID())),
										builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
								builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
										EdalClasses.valueOf(edalPermission.getActionClass().getSimpleName()))),
						builder.equal(permissionRoot.get(STRING_INTERN_METHOD), Methods.valueOf(edalPermission.getActionMethod().getName()))),
				builder.equal(permissionRoot.get(STRING_INTERN_VERSION), edalPermission.getVersion()));
		
		EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();

		if (permission == null) {
			throw new PrimaryDataEntityException(
					"couldn't found method permission to delete " + edalPermission.getActionMethod().getName());
		}
		Transaction transaction = session.beginTransaction();
		// delete Permission
		session.delete(permission);
		transaction.commit();
		session.close();

	}

	/** {@inheritDoc} */
	@Override
	public void revokePermission(String principalType, String principalName, PrimaryDataEntity entity)
			throws PrimaryDataEntityException {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get(STRING_NAME),principalName)),builder.equal(principalRoot.get(STRING_TYPE), principalType));

		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PRINCIPALS).uniqueResult();
		
		if (principal == null) {
			throw new PrimaryDataEntityException("couldn't found the correct principal to delete permission");
		}

		for (final Methods method : GrantableMethods.ENTITY_METHODS) {

			CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
			Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
			permissionCriteria.where(
					builder.and(
							builder.and(
									builder.and(
											builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
													entity.getID())),
											builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
									builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
											EdalClasses.PrimaryDataEntity)),
							builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
					builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
			
			EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
					
			// if permission exists -> delete
			if (permission != null) {
				Transaction transaction = session.beginTransaction();
				session.delete(permission);
				transaction.commit();
			}

		}

		if (entity.isDirectory()) {
			for (final Methods method : GrantableMethods.DIRECTORY_METHODS) {

				CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
				Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
				permissionCriteria.where(
						builder.and(
								builder.and(
										builder.and(
												builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
														entity.getID())),
												builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
										builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
												EdalClasses.PrimaryDataDirectory)),
								builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
						builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
				
				EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
				
				// if permission exists -> delete
				if (permission != null) {
					Transaction transaction = session.beginTransaction();
					session.delete(permission);
					transaction.commit();
				}
			}
		}

		if (!entity.isDirectory()) {
			for (final Methods method : GrantableMethods.FILE_METHODS) {

				CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder.createQuery(EdalPermissionImplementation.class);
				Root<EdalPermissionImplementation> permissionRoot = permissionCriteria.from(EdalPermissionImplementation.class);
				permissionCriteria.where(
						builder.and(
								builder.and(
										builder.and(
												builder.and(builder.equal(permissionRoot.get(STRING_INTERN_ID),
														entity.getID())),
												builder.equal(permissionRoot.get(STRING_PRINCIPAL), principal)),
										builder.equal(permissionRoot.get(STRING_INTERN_CLASS),
												EdalClasses.PrimaryDataFile)),
								builder.equal(permissionRoot.get(STRING_INTERN_METHOD), method)),
						builder.equal(permissionRoot.get(STRING_INTERN_VERSION), entity.getCurrentVersion().getRevision()));
				
				EdalPermissionImplementation permission = session.createQuery(permissionCriteria).setCacheable(true).setCacheRegion(CACHE_REGION_FOR_PERMISSIONS).uniqueResult();
	
				// if permission exists -> delete
				if (permission != null) {
					Transaction transaction = session.beginTransaction();
					session.delete(permission);
					transaction.commit();
				}
			}
		}
		session.close();
	}

	/** {@inheritDoc} */
	@Override
	public void setPermissionObjectID(String id) {
		PermissionProviderImplementation.getThreadlocalentityid().set(id);

	}

	/** {@inheritDoc} */
	@Override
	public void storeRootUser(Subject subject, InternetAddress address, UUID uuid) throws EdalException {

		Principal principal = null;

		for (final Principal p : subject.getPrincipals()) {
			principal = p;
			break;
		}
		if (principal == null) {
			throw new EdalException("could not get the current principal from the authenticated subject");
		}

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		Transaction transaction = session.beginTransaction();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<RootImplementation> rootCriteria = builder.createQuery(RootImplementation.class);
		Root<RootImplementation> rootRoot = rootCriteria.from(RootImplementation.class);

		rootCriteria.select(rootRoot);

		RootImplementation existingRoot = session.createQuery(rootCriteria).setCacheable(true).uniqueResult();

		if (existingRoot != null) {
			session.delete(existingRoot);
		}

		RootImplementation newRoot = new RootImplementation(principal.getName(), principal.getClass().getSimpleName(),
				address, uuid.toString());

		session.save(newRoot);

		transaction.commit();

		session.close();

	}

	/** {@inheritDoc} */
	@Override
	public boolean validateRootUser(InternetAddress address, UUID uuid) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		Transaction transaction = session.beginTransaction();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<RootImplementation> rootCriteria = builder.createQuery(RootImplementation.class);
		Root<RootImplementation> rootRoot = rootCriteria.from(RootImplementation.class);

		rootCriteria.select(rootRoot);

		RootImplementation rootUser = session.createQuery(rootCriteria).setCacheable(true).uniqueResult();

		if (rootUser.getAddress().equals(address.getAddress()) && rootUser.getUuid().equals(uuid.toString())) {
			rootUser.setValidated(true);
			session.update(rootUser);
			transaction.commit();
			session.close();
			return true;
		} else {
			transaction.commit();
			session.close();
			return false;
		}

	}

	/** {@inheritDoc} */
	@Override
	public InternetAddress getCurrentRootUser() throws EdalException {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<RootImplementation> rootCriteria = builder.createQuery(RootImplementation.class);
		Root<RootImplementation> rootRoot = rootCriteria.from(RootImplementation.class);

		rootCriteria.select(rootRoot);

		RootImplementation rootUser = session.createQuery(rootCriteria).setCacheable(true).uniqueResult();
		
		session.close();

		if (rootUser == null) {
			return null;
		}

		else {
			InternetAddress address = null;
			try {
				address = new InternetAddress(rootUser.getAddress());
			} catch (AddressException | NullPointerException e) {
				// throw new EdalException("unable to load email address: "
				// + e.getMessage(), e);
				return null;
			}
			return address;
		}

	}

	/** {@inheritDoc} */
	@Override
	public boolean isRootValidated(InternetAddress address) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<RootImplementation> rootCriteria = builder.createQuery(RootImplementation.class);
		Root<RootImplementation> rootRoot = rootCriteria.from(RootImplementation.class);

		rootCriteria.select(rootRoot);

		RootImplementation rootUser = session.createQuery(rootCriteria).setCacheable(true).uniqueResult();

		session.close();

		if (rootUser.getAddress().equals(address.getAddress()) && rootUser.isValidated()) {

			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("root user validated");
			return true;
		} else {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Class<? extends Principal>> getSupportedPrincipals() throws EdalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
	
		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<SupportedPrincipals> principalCriteria = builder.createQuery(SupportedPrincipals.class);
		Root<SupportedPrincipals> principalRoot = principalCriteria.from(SupportedPrincipals.class);

		principalCriteria.select(principalRoot);

		final List<SupportedPrincipals> privatePrincipals = session.createQuery(principalCriteria).list();

		session.close();

		if (privatePrincipals.isEmpty()) {
			throw new EdalException("Unable to load all supported Principals : no type stored");
		}

		final List<Class<? extends Principal>> list = new ArrayList<Class<? extends Principal>>(
				privatePrincipals.size());

		for (final SupportedPrincipals principal : privatePrincipals) {
			try {
				list.add((Class<? extends Principal>) Class.forName(principal.getName()));
			} catch (final ClassNotFoundException e) {
				throw new EdalException("Unable to load all supported Principals", e);
			}
		}
		return list;

	}

}