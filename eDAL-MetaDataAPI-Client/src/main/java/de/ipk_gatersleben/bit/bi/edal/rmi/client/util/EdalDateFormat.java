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