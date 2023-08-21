package com.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
//@Profile(value = {"development", "production"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	private static String REALM = "REAME";
	
	private static final String[] USER_MATCHER = { "/prezzo/promo/**", "/promo/cerca/**" };
	private static final String[] ADMIN_MATCHER = { "/promo/inserisci/**", "/promo/elimina/**"};
	private static final String[] AUTH_WHITELIST = {
		// -- swagger ui
		"/api/",
		"/api/**",
		"/v2/api-docs",
		"/swagger-resources",
		"/swagger-resources/**",
		"/configuration/ui",
		"/configuration/security",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/webjars/**"
	};
	
	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
			http.csrf().disable()
				.authorizeRequests()
				.antMatchers(USER_MATCHER).hasAnyRole("USER")
				.antMatchers(ADMIN_MATCHER).hasAnyRole("ADMIN")
				.antMatchers(AUTH_WHITELIST).permitAll()
				// .anyRequest().authenticated()
				.antMatchers("/**").authenticated()
				.and()
				.httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint()).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Autowired
	  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception 
	  {
	    	auth
	    		.userDetailsService(userDetailsService)
	    		.passwordEncoder(new BCryptPasswordEncoder());
	  }
	
	@Bean
	public AuthEntryPoint getBasicAuthEntryPoint()
	{
		return new AuthEntryPoint();
	}

	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	};

}
