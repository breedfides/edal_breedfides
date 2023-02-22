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

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JwtValidator {

	private static final List<String> allowedIssesuer = Collections.singletonList("BreedFides");

	/**
	 * Validate a JWT token
	 * 
	 * @param token
	 * @return decoded token
	 * @throws Exception if something goes wrong
	 */
	public static DecodedJWT validate(String token) throws Exception {
		try {
			final DecodedJWT jwt = JWT.decode(token);

			if (!allowedIssesuer.contains(jwt.getIssuer())) {
				throw new InvalidParameterException(String.format("Unknown Issuer %s", jwt.getIssuer()));
			}

			RSAPublicKey publicKey = (RSAPublicKey) KeyGenerator.loadKeyPair().getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) KeyGenerator.loadKeyPair().getPrivate();

			Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwt.getIssuer()).build();

			verifier.verify(token);

			return jwt;

		} catch (JWTVerificationException e) {
			throw new Exception("JWT validation failed: " + e.getMessage());

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			throw new Exception("Loading KeyPair failes" + e.getMessage());
		}
	}

	public static String getSerialNumber(String token) throws Exception {
		
		System.out.println("HERE");
		
		try {
			final DecodedJWT jwt = JWT.decode(token);

			if (!allowedIssesuer.contains(jwt.getIssuer())) {
				throw new InvalidParameterException(String.format("Unknown Issuer %s", jwt.getIssuer()));
			}

			RSAPublicKey publicKey = (RSAPublicKey) KeyGenerator.loadKeyPair().getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) KeyGenerator.loadKeyPair().getPrivate();

			Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwt.getIssuer()).build();

			verifier.verify(token);

			return jwt.getClaim("serialNumber").asString();

		} catch (JWTVerificationException e) {
			throw new Exception("JWT validation failed: " + e.getMessage());

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			throw new Exception("Loading KeyPair failes" + e.getMessage());
		}
	}

}
