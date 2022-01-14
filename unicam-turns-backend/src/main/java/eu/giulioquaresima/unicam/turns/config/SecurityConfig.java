package eu.giulioquaresima.unicam.turns.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import eu.giulioquaresima.unicam.turns.security.CertificatesLocator;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.inMemoryAuthentication().withUser("giulio").password("{noop}giulio").roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
			.cors()
			.and().csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().authorizeRequests().anyRequest().authenticated()
//			.and().oauth2Login()
			.and().oauth2ResourceServer().jwt();
	}
	
	@Bean
	public JwtDecoder jwtDecoder(@Autowired CertificatesLocator certificatesLocator) 
	{
		return NimbusJwtDecoder.withPublicKey(certificatesLocator.getJwtValidationKey()).build();
	}
	
//	@Bean
//	public ClientRegistrationRepository clientRegistrationRepository() 
//	{
//		return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//	}
//
// 	private ClientRegistration googleClientRegistration() 
// 	{
// 		return ClientRegistration.withRegistrationId("google")
// 			.clientId("google-client-id")
// 			.clientSecret("google-client-secret")
// 			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
// 			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
// 			.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
// 			.scope("openid", "profile", "email", "address", "phone")
// 			.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
// 			.tokenUri("https://www.googleapis.com/oauth2/v4/token")
// 			.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
// 			.userNameAttributeName(IdTokenClaimNames.SUB)
// 			.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
// 			.clientName("Google")
// 			.build();
//	}
 	
}