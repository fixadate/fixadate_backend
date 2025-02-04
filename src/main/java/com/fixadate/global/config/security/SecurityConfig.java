package com.fixadate.global.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.fixadate.domain.dates.repository.TeamGradePermissionsRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.service.repository.PlansPermissionsRepository;
import com.fixadate.global.filter.*;
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
	private final PlansPermissionsRepository plansPermissionsRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final TeamGradePermissionsRepository teamGradePermissionsRepository;
	private static final String[] PERMIT_URL_ARRAY = {
		/* swagger v3 */
		"/swagger-ui/**",
		"/swagger-resources/**",
		"/v3/api-docs/**",
		"/swagger-ui/index.html/**",
		"/favicon.ico",
		/* google calendar */
		"/v1/google/**",
		"/google/**",
		"/oauth2Login/**",
		"/oauth2callback/**",

		/* signIn */
		"/v1/auth/signin",
		"/v1/auth/signup",
		/* etc */
		"/v1/member/nickname",
		"/v1/appVersion",
		"/error",
		"/v1/healthcheck",
		"/v1/servertime",

		/* reissue */
		"/v1/auth/reissue",

		/* logout */
		"/v1/auth/logout",
		"/v1/google/loadtest"
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
			.exceptionHandling(handling -> handling
				.accessDeniedHandler(jwtAccessDeniedHandler)
				.authenticationEntryPoint(authenticationEntryPoint))
			.addFilterBefore(new TeamAuthorizationFilter(teamMembersRepository, teamGradePermissionsRepository), SubscriptionAuthorizationFilter.class)
			.addFilterBefore(new SubscriptionAuthorizationFilter(plansPermissionsRepository), JwtAuthenticationFilter.class)
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
			.addFilterBefore(new NativeAuthenticationFilter(), ExceptionHandlerFilter.class)
			.build();
	}
}
