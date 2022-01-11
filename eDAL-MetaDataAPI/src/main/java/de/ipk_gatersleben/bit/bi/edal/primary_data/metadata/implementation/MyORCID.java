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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;

@Entity
@Table(name = "ORCID")
public class MyORCID implements Serializable {

	private static final long serialVersionUID = -5855941626248641032L;
	private Integer id;
	private String orcid;
	private Set<MyNaturalPerson> persons;

	public MyORCID() {

	}

	public MyORCID(String orcid) {
		this.orcid = orcid;
	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	@Id
	@GeneratedValue
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(targetEntity = MyNaturalPerson.class, mappedBy = "orcid")
	public Set<MyNaturalPerson> getPersons() {
		return persons;
	}

	public void setPersons(Set<MyNaturalPerson> persons) {
		this.persons = persons;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof MyORCID))
			return false;

		final MyORCID orcid = (MyORCID) other;

		if (!orcid.getOrcid().equals(this.getOrcid()))
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = this.getOrcid().hashCode();
		return result;
	}

	public ORCID toORCID() throws ORCIDException {
		return new ORCID(this.getOrcid());
	}

}
