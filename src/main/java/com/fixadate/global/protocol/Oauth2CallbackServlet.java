package com.fixadate.global.protocol;

import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/oauth2callback")
@Slf4j
public class Oauth2CallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws IOException {
        String userId = req.getSession().getId();
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
        return GoogleApiConfig.initializeFlow();

    }

    @Override
    protected String getRedirectUri(HttpServletRequest var1) {
        return GoogleApiConfig.getRedirectUri(var1);
    }

    @Override
    protected String getUserId(HttpServletRequest var1) {
        return GoogleApiConfig.getClientId(var1);
    }
}
