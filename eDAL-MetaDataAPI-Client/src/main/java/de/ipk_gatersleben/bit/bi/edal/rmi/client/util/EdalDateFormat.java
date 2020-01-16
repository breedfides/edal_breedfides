/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.text.SimpleDateFormat;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
/**
 * A class used to format Datetime.
 *
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalDateFormat {
	public  static SimpleDateFormat getDefaultDateFormat(EdalDatePrecision _precision) {
		if(_precision==null)
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		if(_precision.equals(EdalDatePrecision.MILLISECOND))
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSSSS");
		}
		else if(_precision.equals(EdalDatePrecision.SECOND))
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		else if(_precision.equals(EdalDatePrecision.MINUTE))
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		else if(_precision.equals(EdalDatePrecision.HOUR))
		{
			return new SimpleDateFormat("yyyy-MM-dd HH");
		}
		else if(_precision.equals(EdalDatePrecision.DAY))
		{
			return new SimpleDateFormat("yyyy-MM-dd");
		}
		else if(_precision.equals(EdalDatePrecision.MONTH))
		{
			return new SimpleDateFormat("yyyy-MM");
		}
		else if(_precision.equals(EdalDatePrecision.YEAR))
		{
			return new SimpleDateFormat("yyyy");
		}
		else if(_precision.equals(EdalDatePrecision.DECADE))
		{
			return new SimpleDateFormat("yyyy");
		}
		else
		{
			return new SimpleDateFormat("yyyy");
		}
	}
}