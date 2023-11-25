package com.fixadate.global.config;

import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.oauth.handler.OAuth2MemberSuccessHandler;
import com.fixadate.global.oauth.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends SecurityConfigurerAdapter {
    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(); // deprecated for spring security 7.0 (available for now)
        http.authorizeHttpRequests().anyRequest().permitAll();
        http.headers(headers -> headers.frameOptions().disable());
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable();
        http.httpBasic().disable();

        http
                .oauth2Login()
                .successHandler(new OAuth2MemberSuccessHandler(jwtProvider))
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();
    }
}
