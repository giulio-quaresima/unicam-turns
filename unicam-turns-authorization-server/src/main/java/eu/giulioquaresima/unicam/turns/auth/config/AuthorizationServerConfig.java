package eu.giulioquaresima.unicam.turns.auth.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import eu.giulioquaresima.unicam.turns.auth.security.CertificatesLocator;

/**
 * 
 * @author Joe Grandja
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Configuration
//@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig 
{
	@Autowired
	private CertificatesLocator certificatesLocator;
	
	@Value ("${eu.giulioquaresima.unicam.turns.oauth2.openid.clients.unicam-turns-app.client-id:unicam-turns-app}")
	private String clientId;
	
	@Value ("${eu.giulioquaresima.unicam.turns.oauth2.openid.clients.unicam-turns-app.redirect-uri:http://unicam-turns-app:8100}")
	private String redirectUri;
	
	@Value ("${eu.giulioquaresima.unicam.turns.oauth2.openid.server.issuer-url:http://unicam-turns-authorization-server:9000}")
	private String issuerUrl;
	
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception 
    {
    	OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(
    			http
    			.cors() // See also eu.giulioquaresima.unicam.turns.auth.config.WebMvcConfiguration.addCorsMappings(CorsRegistry)
    			.and()
    			);
		return http.formLogin(Customizer.withDefaults()).build();
    }
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() 
    {
    	// https://docs.spring.io/spring-security/site/docs/5.2.12.RELEASE/reference/html/oauth2.html#oauth2login-boot-property-mappings
    	RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
    		.clientId(clientId)
    		.clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // PKCE, no client authentication
    		.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//    		.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//    		.redirectUri("https://oidcdebugger.com/debug")
    		.redirectUri(redirectUri)
    		.scope(OidcScopes.OPENID)
    		.scope(OidcScopes.PROFILE)
    		.scope(OidcScopes.EMAIL)
    		.tokenSettings(TokenSettings
    				.builder()
    				/*
    				 * FIXME #1
    				 * Due to https://github.com/spring-projects/spring-authorization-server/issues/297
    				 * there is no longer support for refresh_token with public clients, 
    				 * as a workaround I set a ridiculously log lifetime span to the access_token (one year!)
    				 */
    				.accessTokenTimeToLive(Duration.ofDays(365)) // <-- ridiculously log lifetime span
//    				.reuseRefreshTokens(false)
//    				.refreshTokenTimeToLive(Duration.ofMinutes(5))
    				.build()
    				)
    		.build();
    	return new InMemoryRegisteredClientRepository(registeredClient);
    }
    
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
          .issuer(issuerUrl)
          .build();
    }
    
    @Bean
    public JWKSource<SecurityContext> jwkSource() 
    {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    protected RSAKey generateRsa() 
    {
        return new RSAKey.Builder(certificatesLocator.getJwtValidationKey())
          .privateKey(certificatesLocator.getJwtSigningKey())
          .keyID(certificatesLocator.getKeyAlias())
          .build();
    }
    
}