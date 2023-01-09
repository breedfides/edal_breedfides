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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Provides an empty meta data type.
 * 
 * @author lange
 * @author arendd
 */
public class EmptyMetaData extends UntypedData {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY = "none";

    /**
     * Constructor for EmptyMetaData.
     */
    public EmptyMetaData() {
	super(EmptyMetaData.EMPTY);
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final UntypedData datatype) {

	if (datatype instanceof EmptyMetaData) {
	    return EmptyMetaData.EMPTY.compareTo(EmptyMetaData.EMPTY);
	} else {
	    return super.compareTo(datatype);
	}
    }
}