/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

@Entity
@DiscriminatorValue("16")
@Indexed
public class MySubjects extends MyUntypedData {

	private static final long serialVersionUID = 3989021444510959618L;
	private List<MyUntypedData> subjects;

	/**
	 * Default constructor is necessary for <em>HIBERNATE</em> PojoInstantiator.
	 */
	public MySubjects() {
		this.setSubjects(new LinkedList<MyUntypedData>());
	}

	/**
	 * Copy constructor to convert public {@link Subjects} to private
	 * {@link MySubjects}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MySubjects(final UntypedData edal) {

		super(edal);

		if (edal instanceof Subjects) {

			Subjects publicSubjects = (Subjects) edal;

			List<MyUntypedData> privateSubjects = new LinkedList<MyUntypedData>();

			if (!publicSubjects.getSubjects().isEmpty()) {
				for (UntypedData data : publicSubjects.getSubjects()) {

					privateSubjects.add(new MyUntypedData(data.getString()));
				}
			}
			this.setSubjects(privateSubjects);
		}
	}

	public Subjects toSubjects() {

		Subjects subjects = new Subjects();

		for (MyUntypedData data : this.getSubjects()) {
			subjects.add(new UntypedData(data.getString()));
		}

		subjects.setString(this.getString());

		return subjects;
	}

	/**
	 * @return the subjects
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UntypedData_Subjects", joinColumns = @JoinColumn(name = "UNTYPEDDATA_ID"))
	@OrderBy
	public List<MyUntypedData> getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects
	 *            the subjects to set
	 */
	public void setSubjects(List<MyUntypedData> subjects) {
		this.subjects = subjects;
	}
}
