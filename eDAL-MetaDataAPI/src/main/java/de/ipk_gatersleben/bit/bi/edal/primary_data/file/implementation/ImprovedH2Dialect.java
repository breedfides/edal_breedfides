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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.util.Arrays;

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

		res.append(" add constraint if not exists ").append(quote(constraintName)).append(" foreign key (").append(StringHelper.join(", ", Arrays.asList(foreignKey).iterator())).append(") references ").append(referencedTable);

		if (!referencesPrimaryKey) {
			res.append(" (").append(StringHelper.join(", ", Arrays.asList(primaryKey).iterator())).append(')');
		}

		return res.toString();
	}
}