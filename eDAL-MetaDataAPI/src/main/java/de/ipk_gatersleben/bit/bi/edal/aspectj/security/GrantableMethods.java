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
package de.ipk_gatersleben.bit.bi.edal.aspectj.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * Class that provide all API methods as {@link Enum} constant to use them for
 * the
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission}.
 * 
 * @author arendd
 *         <p>
 *         <b>Note:</b> 'getID' and 'getMetaData' are no longer protected
 *         because getID() is necessary for move() and getMetaData() for
 *         isDirectory() with RMI.
 *         <p>
 *         <b>Note:</b> 'getPermissions' is no longer protected because it is
 *         necessary for File Chooser GUI.
 */
public final class GrantableMethods {
	/**
	 * Constant {@link Enum} list of all action classes.
	 */
	public enum EdalClasses {
		/**
		 * {@link PrimaryDataEntity}
		 */
		PrimaryDataEntity,
		/**
		 * {@link PrimaryDataDirectory}
		 */
		PrimaryDataDirectory,
		/**
		 * {@link PrimaryDataFile}
		 */
		PrimaryDataFile
	}

	/**
	 * Constant {@link Enum} list of all grantable API methods.
	 */
	public enum Methods {
		/**
		 * {@link PrimaryDataEntity#addPublicReference(PersistentIdentifier)}
		 */
		addPublicReference,
		/**
		 * {@link PrimaryDataEntity#getPublicReferences()}
		 */
		getPublicReferences,
		/**
		 * {@link PrimaryDataDirectory#createPrimaryDataDirectory(String)}
		 */
		createPrimaryDataDirectory,
		/**
		 * {@link PrimaryDataDirectory#createPrimaryDataFile(String)}
		 */
		createPrimaryDataFile,
		/**
		 * {@link PrimaryDataEntity#delete()}
		 */
		delete,
		/**
		 * {@link PrimaryDataDirectory#exist(String)}
		 */
		exist,
		/**
		 * {@link PrimaryDataEntity#getParentDirectory()}
		 */
		getParentDirectory,
		/**
		 * {@link PrimaryDataDirectory#getPrimaryDataEntity(String)}
		 */
		getPrimaryDataEntity,
		/**
		 * {@link PrimaryDataDirectory#listPrimaryDataEntities()}
		 */
		listPrimaryDataEntities,
		/**
		 * {@link PrimaryDataFile#read(OutputStream)}
		 */
		read,
		/**
		 * {@link PrimaryDataFile#store(InputStream)}
		 */
		store,
		/**
		 * {@link PrimaryDataDirectory#searchByDublinCoreElement(EnumDublinCoreElements, UntypedData, boolean, boolean)}
		 */
		searchByDublinCoreElement,
		/**
		 * {@link PrimaryDataDirectory#searchByMetaData(MetaData, boolean, boolean)}
		 */
		searchByMetaData,
		/**
		 * {@link PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)}
		 */
		searchByPublicationStatus,
		/**
		 * {@link PrimaryDataDirectory#searchByKeyword(String, boolean, boolean)}
		 */
		searchByKeyword,
		/**
		 * {@link PrimaryDataEntity#move(PrimaryDataDirectory)}
		 */
		move,
		/**
		 * {@link PrimaryDataEntity#rename(String)}
		 */
		rename,
		/**
		 * {@link PrimaryDataEntity#setMetaData(MetaData)}
		 * {@link PrimaryDataDirectory#setMetaData(MetaData)}
		 */
		setMetaData,
		/**
		 * {@link PrimaryDataEntity#grantPermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
		 */
		grantPermission,
		/**
		 * {@link PrimaryDataEntity#revokePermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
		 */
		revokePermission,

		ALL;

		public Method getImplClass(Class<? extends PrimaryDataEntity> clazz) throws SecurityException, NoSuchMethodException {

			switch (this) {
			case createPrimaryDataDirectory:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), String.class);
			case setMetaData: {
				if (clazz.getSuperclass().equals(PrimaryDataFile.class) || clazz.equals(PrimaryDataFile.class)) {
					return PrimaryDataFile.class.getDeclaredMethod(this.name(), MetaData.class);
				} else if (clazz.getSuperclass().equals(PrimaryDataDirectory.class) || clazz.equals(PrimaryDataDirectory.class)) {
					return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), MetaData.class);
				}
			}
			case createPrimaryDataFile:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), String.class);
			case delete:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), new Class<?>[0]);
			case exist:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), String.class);
			case addPublicReference:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), PersistentIdentifier.class);
			case getParentDirectory:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), new Class<?>[0]);
			case getPrimaryDataEntity:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), String.class);
			case getPublicReferences:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), new Class<?>[0]);
			case grantPermission:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), Principal.class, Methods.class);
			case listPrimaryDataEntities:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), new Class<?>[0]);
			case move:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), PrimaryDataDirectory.class);
			case read:
				return PrimaryDataFile.class.getDeclaredMethod(this.name(), OutputStream.class);
			case rename:
				return PrimaryDataEntity.class.getDeclaredMethod("rename", String.class);
			case revokePermission:
				return PrimaryDataEntity.class.getDeclaredMethod(this.name(), Principal.class, Methods.class);
			case searchByDublinCoreElement:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), EnumDublinCoreElements.class, UntypedData.class, boolean.class, boolean.class);
			case searchByMetaData:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), MetaData.class, boolean.class, boolean.class);
			case searchByPublicationStatus:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), PublicationStatus.class);
			case searchByKeyword:
				return PrimaryDataDirectory.class.getDeclaredMethod(this.name(), String.class, boolean.class, boolean.class);
			case store:
				return PrimaryDataFile.class.getDeclaredMethod("store", InputStream.class);
			case ALL:
				break;
			default:
				break;
			}
			return null;
		}
	}

	/**
	 * Map of all methods of {@link PrimaryDataEntity}.
	 */
	public static final List<Methods> ENTITY_METHODS;
	/**
	 * Map of all methods of {@link PrimaryDataDirectory}.
	 */
	public static final List<Methods> DIRECTORY_METHODS;
	/**
	 * Map of all methods of {@link PrimaryDataFile}.
	 */
	public static final List<Methods> FILE_METHODS;
	/**
	 * Map to find the EnumMap for every class.
	 */
	public static final Map<Class<? extends PrimaryDataEntity>, List<Methods>> CLASS_MAP;

	static {

		ENTITY_METHODS = new ArrayList<Methods>();
		DIRECTORY_METHODS = new ArrayList<Methods>();
		FILE_METHODS = new ArrayList<Methods>();

		ENTITY_METHODS.add(Methods.addPublicReference);
		ENTITY_METHODS.add(Methods.delete);
		ENTITY_METHODS.add(Methods.getPublicReferences);
		ENTITY_METHODS.add(Methods.getParentDirectory);
		ENTITY_METHODS.add(Methods.grantPermission);
		ENTITY_METHODS.add(Methods.move);
		ENTITY_METHODS.add(Methods.rename);
		ENTITY_METHODS.add(Methods.revokePermission);
		DIRECTORY_METHODS.add(Methods.createPrimaryDataDirectory);
		DIRECTORY_METHODS.add(Methods.createPrimaryDataFile);
		DIRECTORY_METHODS.add(Methods.exist);
		DIRECTORY_METHODS.add(Methods.getPrimaryDataEntity);
		DIRECTORY_METHODS.add(Methods.listPrimaryDataEntities);
		DIRECTORY_METHODS.add(Methods.searchByDublinCoreElement);
		DIRECTORY_METHODS.add(Methods.searchByMetaData);
		DIRECTORY_METHODS.add(Methods.searchByPublicationStatus);
		DIRECTORY_METHODS.add(Methods.setMetaData);
		FILE_METHODS.add(Methods.read);
		FILE_METHODS.add(Methods.setMetaData);
		FILE_METHODS.add(Methods.store);

		CLASS_MAP = new HashMap<>();
		CLASS_MAP.put(PrimaryDataEntity.class, ENTITY_METHODS);
		CLASS_MAP.put(PrimaryDataDirectory.class, DIRECTORY_METHODS);
		CLASS_MAP.put(PrimaryDataFile.class, FILE_METHODS);
	}
}