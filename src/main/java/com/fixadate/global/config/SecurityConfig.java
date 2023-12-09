package com.fixadate.global.config;

import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.jwt.CustomAuthenticationEntryPoint;
import com.fixadate.global.jwt.JwtAccessDeniedHandler;
import com.fixadate.global.jwt.filter.JwtAuthenticationFilter;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.oauth.handler.OAuth2MemberFailureHandler;
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
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .logout()
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
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated();

//        http
//                .oauth2Login()
//                .successHandler(new OAuth2MemberSuccessHandler(jwtProvider))
//                .failureHandler(new OAuth2MemberFailureHandler())
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService);
        return http.build();
    }
}
