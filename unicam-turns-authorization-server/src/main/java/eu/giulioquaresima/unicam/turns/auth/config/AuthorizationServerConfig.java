package eu.giulioquaresima.unicam.turns.auth.config;

import java.util.Collections;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
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
            .formLogin(Customizer.withDefaults())
            ;
 
        return http.build();
    }
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() 
    {
    	// https://docs.spring.io/spring-security/site/docs/5.2.12.RELEASE/reference/html/oauth2.html#oauth2login-boot-property-mappings
    	RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
    		.clientId("unicam-turns-app")
//    		.clientSecret("{noop}mimportassaitantosonosolounambientedisviluppo")
//    		.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
    		.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
    		.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    		.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
    		.authorizationGrantType(AuthorizationGrantType.PASSWORD)
    		//.clientSettings(ClientSettings.builder().requireProofKey(false))
    		.redirectUri("https://oidcdebugger.com/debug")
//          .redirectUri("http://127.0.0.1:8080/login/oauth2/code/articles-client-oidc")
//          .redirectUri("http://127.0.0.1:8080/authorized")
    		.scope(OidcScopes.OPENID)
    		.scope(OidcScopes.PROFILE)
    		.scope(OidcScopes.EMAIL)
//          .scope("articles.read")
    		.build();
    	return new InMemoryRegisteredClientRepository(registeredClient);
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
    	/*
    	InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
    	userDetailsManager.createUser(User
    			.withUsername("giulio")
    			.password("{noop}giulio")
    			.roles("ADMIN")
    			.build());
    	return userDetailsManager;
    	*/
    	return new RidiculousUserDetailsService();
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
    
    /**
     * I'm authenticating everybody!
     * 
     * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
     */
    public static class RidiculousUserDetailsService implements UserDetailsService
    {
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
		{
			return new User(username, "{noop}" + username, Collections.emptySet());
		}
    }

}