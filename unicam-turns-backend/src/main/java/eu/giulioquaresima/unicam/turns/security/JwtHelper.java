package eu.giulioquaresima.unicam.turns.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * A helper class to create JWTs
 *
 * @author Imesha Sudasingha
 * @author Giulio Quaresima
 * 
 * @see https://medium.com/swlh/stateless-jwt-authentication-with-spring-boot-a-better-approach-1f5dbae6c30f
 */
@Component
public class JwtHelper 
{
	@Autowired
	private CertificatesLocator certificatesLocator;
	
	public String createJwtForClaims(String subject, Map<String, String> claims, Duration duration) 
	{
		JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);
		claims.forEach(jwtBuilder::withClaim);
		Instant now = Instant.now();
		return jwtBuilder
				.withNotBefore(Date.from(now))
				.withExpiresAt(Date.from(now.plus(duration)))
				.sign(Algorithm.RSA256(certificatesLocator.getJwtValidationKey(), certificatesLocator.getJwtSigningKey()));
	}
}
