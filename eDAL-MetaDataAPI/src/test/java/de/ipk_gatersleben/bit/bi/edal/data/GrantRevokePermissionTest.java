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
package de.ipk_gatersleben.bit.bi.edal.data;

import java.security.AccessControlException;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

/**
 * Test the
 * {@link PrimaryDataEntity#grantPermission(java.security.Principal, Methods)}
 * and
 * {@link PrimaryDataEntity#revokePermission(java.security.Principal, Methods)}
 * functions.
 * 
 * @author arendd
 */
public class GrantRevokePermissionTest extends EdalTestCaseWithoutShutdown {

	public GrantRevokePermissionTest() {
		super();
	}

	private static final boolean CLEAN_DATABASE_BEFORE_START = true;

	@Test
	public void testGrantRevoke() throws Exception {

		/* Session 1 */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(CLEAN_DATABASE_BEFORE_START, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		rootDirectory.grantPermission(new SamplePrincipal("SampleUser"), Methods.listPrimaryDataEntities);
		rootDirectory.grantPermission(new SamplePrincipal("SampleUser"), Methods.createPrimaryDataFile);

		/*
		 * create a subDirectory named by the method which is granted for the
		 * Directory
		 */
		for (final Methods entry : GrantableMethods.DIRECTORY_METHODS) {
			PrimaryDataDirectory pdd = null;
			pdd = rootDirectory.createPrimaryDataDirectory(entry.toString());
			pdd.grantPermission(new SamplePrincipal("SampleUser"), entry);
			pdd.grantPermission(new SamplePrincipal("SampleUser"), Methods.revokePermission);

		}
		DataManager.shutdown();

		/* Session 2 */
		PrimaryDataDirectory rootDirectory2 = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(false, this.configuration), EdalHelpers.authenticateSampleUser());

		List<PrimaryDataEntity> list = rootDirectory2.listPrimaryDataEntities();

		Object[] array = list.toArray();

		/* test all allowed functions */
		try {
			((PrimaryDataDirectory) array[0]).createPrimaryDataDirectory("");
			((PrimaryDataDirectory) array[1]).createPrimaryDataFile("");
			((PrimaryDataDirectory) array[2]).exist("");
			try {
				((PrimaryDataDirectory) array[3]).getPrimaryDataEntity("");
			} catch (PrimaryDataDirectoryException e) {
			}
			((PrimaryDataDirectory) array[4]).listPrimaryDataEntities();
			((PrimaryDataDirectory) array[5]).searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData("test"), true, true);
			((PrimaryDataDirectory) array[6]).searchByMetaData(DataManager.getImplProv().createMetaDataInstance(), false, false);
			((PrimaryDataDirectory) array[7]).searchByPublicationStatus(PublicationStatus.ACCEPTED);

			MetaData meta = DataManager.getImplProv().createMetaDataInstance();
			meta.setElementValue(EnumDublinCoreElements.DATE, ((PrimaryDataDirectory) array[8]).getMetaData().getElementValue(EnumDublinCoreElements.DATE));
			meta.setElementValue(EnumDublinCoreElements.TYPE, MetaData.DIRECTORY);
			meta.setElementValue(EnumDublinCoreElements.FORMAT, MetaData.EMPTY);
			meta.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData("setMetaData"));
			((PrimaryDataDirectory) array[8]).setMetaData(meta);
		} catch (AccessControlException ace) {
			Assertions.fail(ace.getMessage());
		}

		/* test for all function the forbidden getMetaData function */
		try {
			((PrimaryDataDirectory) array[0]).getMetaData();
		} catch (AccessControlException ace0) {
			try {
				((PrimaryDataDirectory) array[1]).getMetaData();
			} catch (AccessControlException ace1) {
				try {
					((PrimaryDataDirectory) array[2]).getMetaData();
				} catch (AccessControlException ace2) {
					try {
						((PrimaryDataDirectory) array[3]).getMetaData();
					} catch (AccessControlException ace3) {
						try {
							((PrimaryDataDirectory) array[4]).getMetaData();
						} catch (AccessControlException ace4) {
							try {
								((PrimaryDataDirectory) array[5]).getMetaData();
							} catch (AccessControlException ace5) {
								try {
									((PrimaryDataDirectory) array[6]).getMetaData();
								} catch (AccessControlException ace6) {
									try {
										((PrimaryDataDirectory) array[7]).getMetaData();
									} catch (AccessControlException ace7) {
										try {
											((PrimaryDataDirectory) array[8]).getMetaData();
										} catch (AccessControlException ace8) {

											Assertions.assertTrue(true);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		/* revoke all granted permissions */
		int i = 0;
		for (final Methods entry : GrantableMethods.DIRECTORY_METHODS) {
			((PrimaryDataDirectory) array[i]).revokePermission(new SamplePrincipal("SampleUser"), entry);
			i++;
			DataManager.getImplProv().getLogger().info("revoked Method : " + entry);

		}
		DataManager.getImplProv().getLogger().info("rekoved all methods");
		/* test again all functions of the directories */
		try {
			DataManager.getImplProv().getLogger().info("try method 1");
			((PrimaryDataDirectory) array[0]).createPrimaryDataDirectory("");
		} catch (Exception ace0) {
			try {
				DataManager.getImplProv().getLogger().info("try method 2");
				((PrimaryDataDirectory) array[1]).createPrimaryDataFile("");
			} catch (Exception ace1) {
				try {
					DataManager.getImplProv().getLogger().info("try method 3");
					((PrimaryDataDirectory) array[2]).exist("");
				} catch (AccessControlException ace2) {
					try {
						try {
							DataManager.getImplProv().getLogger().info("try method 4");
							((PrimaryDataDirectory) array[3]).getPrimaryDataEntity("");
						} catch (PrimaryDataDirectoryException e) {
						}
					} catch (AccessControlException ace3) {
						try {
							DataManager.getImplProv().getLogger().info("try method 5");
							((PrimaryDataDirectory) array[4]).listPrimaryDataEntities();
						} catch (AccessControlException ace4) {
							try {
								DataManager.getImplProv().getLogger().info("try method 6");
								((PrimaryDataDirectory) array[5]).searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData(), true, true);
							} catch (AccessControlException ace5) {
								try {
									DataManager.getImplProv().getLogger().info("try method 7");
									((PrimaryDataDirectory) array[6]).searchByMetaData(DataManager.getImplProv().createMetaDataInstance(), false, false);
								} catch (AccessControlException ace6) {
									try {
										DataManager.getImplProv().getLogger().info("try method 8");
										((PrimaryDataDirectory) array[7]).searchByPublicationStatus(PublicationStatus.ACCEPTED);
									} catch (AccessControlException ace7) {
										try {
											DataManager.getImplProv().getLogger().info("try method 9");
											((PrimaryDataDirectory) array[8]).setMetaData(rootDirectory2.getMetaData().clone());
										} catch (AccessControlException ace8) {
											Assertions.assertTrue(true);
										}
									}
								}

							}
						}
					}
				}
			}

		}

		/* try to revoke grantPermission method of my own Entity */
		/* --> fail */
		PrimaryDataFile pdf = rootDirectory2.createPrimaryDataFile("testfile");

		try {
			pdf.revokePermission(new SamplePrincipal("SampleUser"), Methods.grantPermission);
		} catch (PrimaryDataEntityException e) {
			Assertions.assertTrue(true);
		}

		DataManager.shutdown();
	}
}