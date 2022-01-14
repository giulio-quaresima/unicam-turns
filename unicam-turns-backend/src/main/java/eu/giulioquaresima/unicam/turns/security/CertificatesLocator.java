package eu.giulioquaresima.unicam.turns.security;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configures beans required for JWT signing and validation.
 *
 * @author Giulio Quaresima
 * 
 * @see https://medium.com/swlh/stateless-jwt-authentication-with-spring-boot-a-better-approach-1f5dbae6c30f
 */
@Component
public class CertificatesLocator implements InitializingBean
{
	@Value("${eu.giulioquaresima.unicam.turns.keystore.location:classpath:/eu/giulioquaresima/unicam/turns/security/keystore.jks}")
	private Path keyStorePath;
	
	@Value("${eu.giulioquaresima.unicam.turns.keystore.password:changeit}")
	private String keyStorePassword;
	
	@Value("${eu.giulioquaresima.unicam.turns.key.alias:jwtsigning}")
	private String keyAlias;
	
	@Value("${eu.giulioquaresima.unicam.turns.key.private.passphrase:changeit}")
	private String privateKeyPassphrase;
	
	private RSAPrivateKey jwtSigningKey;
	
	private RSAPublicKey jwtValidationKey;
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		try
		{
			KeyStore keyStore = KeyStore.getInstance(keyStorePath.toFile(), keyStorePassword.toCharArray());
			
			Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());
			if (key instanceof RSAPrivateKey)
			{
				jwtSigningKey = (RSAPrivateKey) key;
			}
			else
			{
				throw new IllegalStateException("Unable to load the RSA private key");
			}
			
			Certificate certificate = keyStore.getCertificate(keyAlias);
			PublicKey publicKey = certificate.getPublicKey();
			if (publicKey instanceof RSAPublicKey) 
			{
				jwtValidationKey = (RSAPublicKey) publicKey;
			}
			else
			{
				throw new IllegalStateException("Unable to load the RSA public key");
			}
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
		catch (KeyStoreException | NoSuchAlgorithmException | CertificateException  e)
		{
			throw new IllegalStateException(e);
		}
	}

	public RSAPrivateKey getJwtSigningKey()
	{
		return jwtSigningKey;
	}

	public RSAPublicKey getJwtValidationKey()
	{
		return jwtValidationKey;
	}

}