//package com.fixadate.global.oauth.handler;
//
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.InternalAuthenticationServiceException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//import static com.fixadate.global.oauth.handler.ErrorMessage.*;
//
//@Component
//@Slf4j
//public class OAuth2MemberFailureHandler extends SimpleUrlAuthenticationFailureHandler {
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//                                        AuthenticationException exception) throws IOException, ServletException {
//        String errorMessage = null;
//        if (exception instanceof InternalAuthenticationServiceException) {
//            errorMessage = BadCredentialsExceptionException;
//        } else if (exception instanceof UsernameNotFoundException) {
//            errorMessage = MemberNotFoundException;
//        } else if(exception instanceof OAuth2AuthenticationException){
//            errorMessage = ErrorMessage;
//            log.info("로그 확인");
//            log.info(exception.toString());
//            log.info(exception.getMessage());
//            log.info(exception.getLocalizedMessage());
//            log.info(exception.getStackTrace().toString());
//        }
//
//        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
//        setDefaultFailureUrl("/auth/login?error=true&exception=" + errorMessage);
//        super.onAuthenticationFailure(request, response, exception);
//    }
//}
