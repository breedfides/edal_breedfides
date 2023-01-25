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
package de.ipk_gatersleben.bit.bi.edal.breedfides.certificate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * Utility class to generate key pair to create certificates
 * 
 * @author arendd
 *
 */
public final class KeyGenerator {

	private static KeyPairGenerator keyPairGenerator;
	private static KeyPair keyPair;

	private static Path privateKeyPath = Paths
			.get(DataManager.getConfiguration().getMountPath().toString() + "key.private");
	private static Path publicKeyPath = Paths
			.get(DataManager.getConfiguration().getMountPath().toString() + "key.public");

	public static void generateKeyPair() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

		if (!Files.exists(privateKeyPath)) {

			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();

			PrivateKey privateKey = keyPair.getPrivate();
			PublicKey publicKey = keyPair.getPublic();

			try (FileOutputStream outPrivate = new FileOutputStream(privateKeyPath.toFile())) {
				outPrivate.write(privateKey.getEncoded());
			}

			try (FileOutputStream outPublic = new FileOutputStream(publicKeyPath.toFile())) {
				outPublic.write(publicKey.getEncoded());
			}

		} else {

			loadKeyPair();
		}
	}

	public static KeyPair loadKeyPair() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

		if (!Files.exists(privateKeyPath)) {
			generateKeyPair();
		}

		KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(privateKeyPath));
		PrivateKey privateKey = privateKeyFactory.generatePrivate(privateKeySpec);

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(publicKeyPath));
		PublicKey publicKey = privateKeyFactory.generatePublic(publicKeySpec);

		keyPair = new KeyPair(publicKey, privateKey);

		return keyPair;
	}

}
