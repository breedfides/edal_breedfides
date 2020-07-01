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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Unique identifier, which is ensures to be global uniqueness defined at <a
 * href="http://www.iso.ch/cate/d2229.html" target="_blank">ISO/IEC
 * 11578:1996</a>.
 * 
 * @author lange
 * @author arendd
 */
public class Identifier extends UntypedData {

	public static final String UNKNOWN_ID = "Unknown_ID";
	
	/**
	 * the generated serial ID
	 */
	private static final long serialVersionUID = 3380495549321758121L;
	private final String id;

	/**
	 * Constructor for Identifier.
	 * 
	 * Set the id to "Unkown_ID".
	 */
	public Identifier() {
		this.id = UNKNOWN_ID;
	}

	/**
	 * Constructor for Identifier with specified id.
	 * 
	 * @param id
	 *            the id to set.
	 */
	public Identifier(String id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Identifier) {

			Identifier identifier = (Identifier) datatype;

			if (id.compareTo(identifier.id) == 0) {
				return super.compareTo(identifier);
			} else {
				return id.compareTo(identifier.id);
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * Getter for the id.
	 * 
	 * @return the id.
	 */
	public final String getID() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
