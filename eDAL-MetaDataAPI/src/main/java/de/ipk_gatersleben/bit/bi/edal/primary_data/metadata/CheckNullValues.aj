/*
 * Copyright (c) 2017 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
