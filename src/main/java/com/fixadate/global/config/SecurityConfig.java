package com.fixadate.global.config;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fixadate.global.jwt.CustomAuthenticationEntryPoint;
import com.fixadate.global.jwt.JwtAccessDeniedHandler;
import com.fixadate.global.jwt.filter.ExceptionHandlerFilter;
import com.fixadate.global.jwt.filter.JwtAuthenticationFilter;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.util.PasswordEncoderFactories;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtProvider jwtProvider;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private static final String[] PERMIT_URL_ARRAY = {
		/* swagger v3 */
		"/swagger-ui/**",
		"/swagger-resources/**",
		"/v3/api-docs/**",
		"/swagger-ui/index.html/**",
		"/favicon.ico",
		/* google calendar */
		"/google/**",
		"/oauth2Login/**",
		"/oauth2callback/**"
		,
		/* signIn */
		"/auth/signin",
		"/auth/signup",
		/* etc */
		"/member/nickname",
		"/error",

		/* reissue */
		"/auth/reissue",

		/* logout */
		"/auth/logout"
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
			.authorizeHttpRequests((req) -> req
				.requestMatchers(PERMIT_URL_ARRAY).permitAll()
				.anyRequest().authenticated())
			.exceptionHandling()
			.accessDeniedHandler(jwtAccessDeniedHandler)
			.authenticationEntryPoint(authenticationEntryPoint)
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
			.build();
	}
}
