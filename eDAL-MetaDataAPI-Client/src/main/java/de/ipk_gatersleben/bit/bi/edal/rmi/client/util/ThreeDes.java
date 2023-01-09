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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;

public class ThreeDes {
	private static final String salat = "12345678";
	private static final String privatekey = "123456789012345678943210";
	private static final String Algorithm = "DESede/CBC/PKCS5Padding";

	public static byte[] encryptMode(String iv, String key, String src) {
		try {
			byte[] keybyte = key.getBytes();
			byte[] rand = new byte[8];
			rand = iv.getBytes();
			IvParameterSpec ivp = new IvParameterSpec(rand);

			DESedeKeySpec dks = new DESedeKeySpec(keybyte);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, securekey, ivp);
			return c1.doFinal(src.getBytes("UTF-8"));
		} catch (java.security.NoSuchAlgorithmException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
		} catch (javax.crypto.NoSuchPaddingException e2) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e2));
		} catch (java.lang.Exception e3) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e3));
		}
		return null;
	}

	public static byte[] decryptMode(String iv, String key, byte[] src) {

		try {
			byte[] srcbytes = src;
			byte[] keybyte = key.getBytes();
			byte[] rand = new byte[8];
			rand = iv.getBytes();
			IvParameterSpec ivp = new IvParameterSpec(rand);
			DESedeKeySpec dks = new DESedeKeySpec(keybyte);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);

			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, securekey, ivp);

			return c1.doFinal(srcbytes);
		} catch (java.security.NoSuchAlgorithmException ex) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(ex));
		} catch (javax.crypto.NoSuchPaddingException ex) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(ex));
		} catch (java.lang.Exception ex) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(ex));
		}
		return null;
	}

	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static final String encodeHex(byte bytes[]) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 16)
				buf.append("0");
			buf.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static String encrypt(String originalstr) {
		byte[] encoded = encryptMode(salat, privatekey, originalstr);
		String encodestr = parseByte2HexStr(encoded);
		return encodestr;
	}

	public static String decrypt(String encodestr)
			throws UnsupportedEncodingException {
		byte[] srcBytes = decryptMode(salat, privatekey,
				parseHexStr2Byte(encodestr));
		return new String(srcBytes, "UTF-8");
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String szSrc = "tomcat80";

		System.out.println("original str:" + szSrc);

		byte[] encoded = encryptMode(salat, privatekey, szSrc);
		String encodestr = parseByte2HexStr(encoded);
		System.out.println("encode str:" + encodestr);

		byte[] srcBytes = decryptMode(salat, privatekey,
				parseHexStr2Byte(encodestr));

		System.out.println("decode str:" + (new String(srcBytes)));

	}
}
