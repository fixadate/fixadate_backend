package com.fixadate.global.config;

import com.fixadate.global.jwt.CustomAuthenticationEntryPoint;
import com.fixadate.global.jwt.JwtAccessDeniedHandler;
import com.fixadate.global.jwt.filter.ExceptionHandlerFilter;
import com.fixadate.global.jwt.filter.JwtAuthenticationFilter;
import com.fixadate.global.jwt.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private final OAuth2MemberSuccessHandler oAuth2MemberSuccessHandler;
//    private final OAuth2MemberFailureHandler oAuth2MemberFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .logout()
                .disable()
                .httpBasic()
                .disable()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)

                .authorizeHttpRequests()
                .requestMatchers("/calendar/google/**", "/calendar/google", "/auth/**", "/", "/member/nickname",
                        "/error", "/swagger-ui/**", "/swagger-resources/**",
                        "/v3/api-docs/**", "/swagger-ui/index.html/**","/google/**","/google","/health-check",
                        "/oauth2Login/**","/login/oauth2/code/google/**","/favicon.ico","/oauth2callback/**").permitAll()
                .anyRequest().authenticated();

//                .and()
//                .oauth2Login()
//                .successHandler(oAuth2MemberSuccessHandler);
//                .failureHandler(oAuth2MemberFailureHandler);
        return http.build();
    }
}
