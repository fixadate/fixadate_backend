package com.fixadate.domain.googleCalendar.protocol;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractAuthorizationCodeCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Lock lock = new ReentrantLock();
    private AuthorizationCodeFlow flow;

    protected AbstractAuthorizationCodeCallbackServlet() {
    }

    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuffer buf = req.getRequestURL();
        if (req.getQueryString() != null) {
            buf.append('?').append(req.getQueryString());
        }

        AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
        String code = responseUrl.getCode();
        if (responseUrl.getError() != null) {
            this.onError(req, resp, responseUrl);
        } else if (code == null) {
            resp.setStatus(400);
            resp.getWriter().print("Missing authorization code");
        } else {
            this.lock.lock();

            try {
                if (this.flow == null) {
                    this.flow = this.initializeFlow();
                }

                String redirectUri = this.getRedirectUri(req);
                TokenResponse response = this.flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
                String userId = this.getUserId(req);
                Credential credential = this.flow.createAndStoreCredential(response, userId);
                this.onSuccess(req, resp, credential);
            } finally {
                this.lock.unlock();
            }
        }

    }

    protected abstract AuthorizationCodeFlow initializeFlow() throws ServletException, IOException;

    protected abstract String getRedirectUri(HttpServletRequest var1) throws ServletException, IOException;

    protected abstract String getUserId(HttpServletRequest var1) throws ServletException, IOException;

    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
    }

    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
    }
}
