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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;

/**
 * Aspect to check that the user can not insert null values for any kind of data
 * type. Check every constructor extended from {@link UntypedData} and every
 * Setter method.
 * 
 * @author arendd
 */
public aspect CheckNullValues {

	pointcut newUntypeData1():  execution(UntypedData+.new (..)) || execution(void UntypedData+.set*(..)) && !cflowbelow(adviceexecution()) && 
						!(execution(MyUntypedData+.new (..)) || execution(void MyUntypedData+.set*(..)));

	@SuppressAjWarnings({ "adviceDidNotMatch" })
	before() : newUntypeData1() {

		for (Object argument : thisJoinPoint.getArgs()) {
			if (argument == null) {
				throwIllegalArgumentException(thisJoinPoint);
			}
		}

	}

	protected void throwIllegalArgumentException(JoinPoint joinPoint)
			throws IllegalArgumentException {
		throw new IllegalArgumentException(
				"no null value allowed for calling: "
						+ joinPoint.getSignature().toShortString());
	}
}
