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
package de.ipk_gatersleben.bit.bi.edal.breedfides.persistence;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;

/**
 * Persistence class for Certificates
 * 
 * @author arendd
 *
 */
@Entity
@Table(name = "CERTIFICATES")
public class Certificate {

	private int id;

	private BigInteger serialNumber;

	public Certificate() {

	}

	@Id
	@GeneratedValue
	private int getId() {
		return id;
	}

	private BigInteger getSerialNumber() {
		return serialNumber;
	}

	private void setId(int id) {
		this.id = id;
	}

	private void setSerialNumber(BigInteger bigInteger) {
		this.serialNumber = bigInteger;
	}

	public static void storeCertificate(X509Certificate x509certificate) {

		Certificate certificate = new Certificate();

		certificate.setSerialNumber(x509certificate.getSerialNumber());

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final Transaction transaction = session.beginTransaction();

		try {
			session.save(certificate);
			transaction.commit();
		} catch (final Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			session.close();
		}
	}

	public static boolean checkSerialNumber(X509Certificate providedCertificate) {
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Certificate> criteria = criteriaBuilder.createQuery(Certificate.class);

		Root<Certificate> root = criteria.from(Certificate.class);
		criteria.where(criteriaBuilder.equal(root.get("serialNumber"), providedCertificate.getSerialNumber()));
		Certificate loadedCertificate = session.createQuery(criteria).getSingleResult();
	
		return loadedCertificate.getSerialNumber().equals(providedCertificate.getSerialNumber());

	}
}
