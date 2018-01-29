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

import org.hibernate.dialect.H2Dialect;
import org.hibernate.internal.util.StringHelper;

/**
 * Workaround.
 * 
 * https://hibernate.onjira.com/browse/HHH-7002 ---------;---------
 * http://stackoverflow.com/questions/23858953/grails-2-4-and-hibernate4-errors
 * -with-run-app
 * 
 */
public class ImprovedH2Dialect extends H2Dialect {
	@Override
	public String getDropSequenceString(String sequenceName) {
		// Adding the "if exists" clause to avoid warnings
		return "drop sequence if exists " + sequenceName;
	}

	@Override
	public boolean dropConstraints() {
		// We don't need to drop constraints before dropping tables, that just
		// leads to error messages about missing tables when we don't have a
		// schema in the database
		return false;
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
		final StringBuilder res = new StringBuilder(40);

		res.append(" add constraint if not exists ").append(quote(constraintName)).append(" foreign key (").append(StringHelper.join(", ", foreignKey)).append(") references ").append(referencedTable);

		if (!referencesPrimaryKey) {
			res.append(" (").append(StringHelper.join(", ", primaryKey)).append(')');
		}

		return res.toString();
	}
}