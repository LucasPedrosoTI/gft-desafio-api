package com.gft.desafioapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gft.desafioapi.service.ClienteService;

@SuppressWarnings("deprecation")
@EnableWebSecurity
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ClienteService clienteService;

	@Autowired
	@Lazy
	BCryptPasswordEncoder passwordEncoder;

	protected static final String[] AUTH_WHITELIST = { "/swagger-ui.html/**", "/configuration/**",
			"/swagger-resources/**", "/v2/api-docs", "/webjars/**", "/cadastrar", "/**/imagem" };

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE",
						"OPTIONS");
			}
		};
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(clienteService).passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()).and()
				.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated().and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().mvcMatchers(HttpMethod.OPTIONS, "/**");
		web.ignoring().antMatchers(AUTH_WHITELIST);
	}

}
