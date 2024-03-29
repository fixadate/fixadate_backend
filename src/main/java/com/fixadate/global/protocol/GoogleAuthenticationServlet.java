package com.fixadate.global.protocol;

import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
// 컴파일러의 경고를 무시하겠다고 하는 것임
@WebServlet(urlPatterns = "/oauth2Login")
public class GoogleAuthenticationServlet extends AbstractAuthorizationCodeServlet {
    @Override
    protected AuthorizationCodeFlow initializeFlow() {
        return GoogleApiConfig.initializeFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest httpServletRequest) {
        return GoogleApiConfig.getRedirectUri(httpServletRequest);
    }

    @Override
    protected String getUserId(HttpServletRequest httpServletRequest) {
        return GoogleApiConfig.getClientId(httpServletRequest);
    }
}
