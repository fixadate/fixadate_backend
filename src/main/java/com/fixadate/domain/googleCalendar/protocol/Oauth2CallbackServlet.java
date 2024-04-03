package com.fixadate.domain.googleCalendar.protocol;

import com.fixadate.global.util.GoogleUtils;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/oauth2callback")
public class Oauth2CallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws IOException {
        String userId = req.getSession().getId();
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", credential.getAccessToken())
                .httpOnly(true)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", credential.getRefreshToken())
                .httpOnly(true)
                .build();
        resp.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        resp.sendRedirect("/google/watch?userId=" + userId);
    }

    /**
     * Handles an error to the authorization, such as when an end user denies authorization.
     */
    @Override
    protected void onError(
            HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        resp.getWriter().print("<p>You Denied Authorization.</p>");
        resp.setStatus(200);
        resp.addHeader("Content-Type", "text/html");
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() {
        return GoogleUtils.initializeFlow();

    }

    @Override
    protected String getRedirectUri(HttpServletRequest var1) {
        return GoogleUtils.getRedirectUri(var1);
    }

    @Override
    protected String getUserId(HttpServletRequest var1) {
        return GoogleUtils.getClientId(var1);
    }
}
