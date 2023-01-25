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

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * Utility class to generate JWT tokens.
 * 
 * @author arendd
 *
 */
public final class JwtGenerator {

	private final static String ISSUER = "BreedFides";

	/**
	 * Generate JWT with limited lifetime. "jti" claim is for a unique identifier of
	 * this token e.g. UUID
	 * 
	 * @param payload            the payload to fill the JWT
	 * @param secondBeforeExpire the length of the lifetime of this JWT
	 * @return the JWT as a {@link String}
	 * @throws Exception if something goes wrong
	 */
	public static String generateJwtWithLimitedLifetime(Map<String, String> payload, int secondBeforeExpire)
			throws Exception {

		Builder tokenBuilder = JWT.create().withIssuer(ISSUER).withClaim("jti", UUID.randomUUID().toString())
				.withExpiresAt(Date.from(Instant.now().plusSeconds(secondBeforeExpire)))
				.withIssuedAt(Date.from(Instant.now()));

		payload.entrySet().forEach(action -> tokenBuilder.withClaim(action.getKey(), action.getValue()));

		return tokenBuilder.sign(Algorithm.RSA256(((RSAPublicKey) KeyGenerator.loadKeyPair().getPublic()),
				((RSAPrivateKey) KeyGenerator.loadKeyPair().getPrivate())));
	}

	/**
	 * Generate JWT with unlimited lifetime. "jti" claim is for a unique identifier
	 * of this token e.g. UUID
	 * 
	 * @param payload the payload to fill the JWT
	 * @return the JWT as a {@link String}
	 * @throws Exception if something goes wrong
	 */
	public static String generateJwt(Map<String, String> payload) throws Exception {

		Builder tokenBuilder = JWT.create().withIssuer(ISSUER).withClaim("jti", UUID.randomUUID().toString())
				.withIssuedAt(Date.from(Instant.now()));

		payload.entrySet().forEach(action -> tokenBuilder.withClaim(action.getKey(), action.getValue()));

		return tokenBuilder.sign(Algorithm.RSA256(((RSAPublicKey) KeyGenerator.loadKeyPair().getPublic()),
				((RSAPrivateKey) KeyGenerator.loadKeyPair().getPrivate())));
	}

	/**
	 * Generate JWT with no payload. "jti" claim is for a unique identifier of this
	 * token e.g. UUID
	 * 
	 * @return the JWT as a {@link String}
	 * @throws Exception if something goes wrong
	 */
	public static String generateLogoutJwt() throws Exception {
		Builder tokenBuilder = JWT.create().withIssuer(ISSUER).withClaim("jti", UUID.randomUUID().toString())
				.withExpiresAt(Instant.now()).withExpiresAt(Date.from(Instant.now()));

		return tokenBuilder.sign(Algorithm.RSA256(((RSAPublicKey) KeyGenerator.loadKeyPair().getPublic()),
				((RSAPrivateKey) KeyGenerator.loadKeyPair().getPrivate())));
	}

}