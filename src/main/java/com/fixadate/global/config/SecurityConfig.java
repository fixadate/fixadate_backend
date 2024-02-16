package com.fixadate.global.config;

import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.jwt.CustomAuthenticationEntryPoint;
import com.fixadate.global.jwt.JwtAccessDeniedHandler;
import com.fixadate.global.jwt.filter.JwtAuthenticationFilter;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.oauth.handler.OAuth2MemberFailureHandler;
import com.fixadate.global.oauth.handler.OAuth2MemberSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2MemberSuccessHandler oAuth2MemberSuccessHandler;
    private final OAuth2MemberFailureHandler oAuth2MemberFailureHandler;
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

                .authorizeHttpRequests()
                .requestMatchers("/calendar/google/**", "/calendar/google", "/auth/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .oauth2Login()
                .successHandler(oAuth2MemberSuccessHandler)
                .failureHandler(oAuth2MemberFailureHandler);

        return http.build();
    }
}
