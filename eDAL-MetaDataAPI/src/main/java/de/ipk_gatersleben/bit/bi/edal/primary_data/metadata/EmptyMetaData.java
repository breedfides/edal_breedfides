/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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