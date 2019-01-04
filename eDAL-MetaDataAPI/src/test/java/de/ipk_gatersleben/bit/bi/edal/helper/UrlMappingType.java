/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.helper;
/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
//package de.ipk_gatersleben.bit.bi.edal.helper;
//
//import java.io.Serializable;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SessionImplementor;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
//import org.hibernate.type.StandardBasicTypes;
//import org.hibernate.usertype.UserType;
//
///**
// * Implementation for own {@link UserType} to store {@link URL} objects as
// * VarChar.
// * 
// * @author arendd
// */
//public class UrlMappingType implements UserType {
//
//	@Override
//	public int[] sqlTypes() {
//		return new int[] { StandardBasicTypes.STRING.sqlType() };
//	}
//
//	@Override
//	public Class<URL> returnedClass() {
//		return URL.class;
//	}
//
//	@Override
//	public boolean equals(Object x, Object y) throws HibernateException {
//		if (x == y)
//			return true;
//		if (x == null || y == null)
//			return false;
//		return false;
//	}
//
//	@Override
//	public int hashCode(Object x) throws HibernateException {
//		return x.hashCode();
//	}
//
////	@Override
////	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
////			throws HibernateException, SQLException {
////
////		URL url = null;
////		try {
////			url = new URL(rs.getString("url"));
////		} catch (MalformedURLException e) {
////			e.printStackTrace();
////		}
////		return url;
////	}
//
////	@Override
////	public void nullSafeSet(PreparedStatement st, Object value, int index)
////			throws HibernateException, SQLException {
////
////		st.setString(index, ((URL) value).toString());
////
////	}
//
//	@Override
//	public Object deepCopy(Object value) throws HibernateException {
//		return value;
//	}
//
//	@Override
//	public boolean isMutable() {
//		return false;
//	}
//
//	@Override
//	public Serializable disassemble(Object value) throws HibernateException {
//		return (Serializable) value;
//	}
//
//	@Override
//	public Object assemble(Serializable cached, Object owner)
//			throws HibernateException {
//		return cached;
//	}
//
//	@Override
//	public Object replace(Object original, Object target, Object owner)
//			throws HibernateException {
//		return original;
//	}
//
//	@Override
//	public Object nullSafeGet(ResultSet arg0, String[] arg1,
//			SessionImplementor arg2, Object arg3) throws HibernateException,
//			SQLException {
//		URL url = null;
//		try {
//			url = new URL(arg0.getString("url"));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		return url;
//	}
//
//	@Override
//	public void nullSafeSet(PreparedStatement st, Object value, int index,
//			SessionImplementor arg3) throws HibernateException, SQLException {
//		st.setString(index, ((URL) value).toString());
//
//	}
//
//	@Override
//	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
//			throws HibernateException, SQLException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
//			throws HibernateException, SQLException {
//		// TODO Auto-generated method stub
//		
//	}
//}