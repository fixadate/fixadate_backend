package com.fixadate.domain.googleCalendar.protocol;

import com.fixadate.global.util.GoogleUtils;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
@Tag(name = "google login endpoint", description = "calendar 권한을 받기 위해 google login을 하는 엔드포인트입니다.")
// 컴파일러의 경고를 무시하겠다고 하는 것임
@WebServlet(urlPatterns = "/oauth2Login")
public class GoogleAuthenticationServlet extends AbstractAuthorizationCodeServlet {
    @Override
    protected AuthorizationCodeFlow initializeFlow() {
        return GoogleUtils.initializeFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest httpServletRequest) {
        return GoogleUtils.getRedirectUri(httpServletRequest);
    }

    @Override
    protected String getUserId(HttpServletRequest httpServletRequest) {
        return GoogleUtils.getClientId(httpServletRequest);
    }
}
