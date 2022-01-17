package eu.giulioquaresima.unicam.turns.auth.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import eu.giulioquaresima.unicam.turns.auth.security.CertificatesLocator;

@Configuration
//@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig 
{
	@Autowired
	private CertificatesLocator certificatesLocator;
	
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception 
    {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() 
    {
    	RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
    		.clientId("unicam-turns-backend")
    		.clientSecret("{noop}secret")
    		.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
    		.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    		.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
    		.redirectUri("https://oidcdebugger.com/debug")
//          .redirectUri("http://127.0.0.1:8080/login/oauth2/code/articles-client-oidc")
//          .redirectUri("http://127.0.0.1:8080/authorized")
    		.scope(OidcScopes.OPENID)
//          .scope("articles.read")
    		.build();
    	return new InMemoryRegisteredClientRepository(registeredClient);
    }
    
    /**
     * @param http
     * @return
     * @throws Exception
     * 
     * @see https://huongdanjava.com/implement-oauth-authorization-server-using-spring-authorization-server.html
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception 
    {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
 
        return http.build();
    }
    
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
          .issuer("http://localhost:9000")
          .build();
    }
    
    @Bean
    public UserDetailsService userDetailsService()
    {
    	InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
    	userDetailsManager.createUser(User
    			.withUsername("giulio")
    			.password("{noop}giulio")
    			.roles("ADMIN")
    			.build());
    	return userDetailsManager;
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