/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.giulioquaresima.unicam.turns.auth.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Joe Grandja
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@EnableWebSecurity
public class DefaultSecurityConfig 
{

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception 
	{
		http
			.authorizeRequests(authorizeRequests ->
				authorizeRequests
					.antMatchers("/favicon.*").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(withDefaults());
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() 
	{
		/*
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user1")
				.password("password")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
		*/
    	return new RidiculousUserDetailsService();
	}
	
    /**
     * I'm authenticating everybody, with each password equal to the respective username!
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