package com.fixadate.domain.googleCalendar.protocol;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.fixadate.global.util.constant.ConstantValue.ID;

public abstract class AbstractAuthorizationCodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Lock lock = new ReentrantLock();
    private Credential credential;
    private AuthorizationCodeFlow flow;

    protected AbstractAuthorizationCodeServlet() {
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        this.lock.lock();

        try {
            String memberId = req.getHeader(ID.getValue());
            String userId = this.getUserId(req);
            if (this.flow == null) {
                this.flow = this.initializeFlow();
            }

            this.credential = this.flow.loadCredential(userId);
            if (this.credential != null && this.credential.getAccessToken() != null) {
                try {
                    resp.setHeader(ID.getValue(), memberId);
                    super.service(req, resp);
                    return;
                } catch (HttpResponseException var8) {
                    if (this.credential.getAccessToken() != null) {
                        throw var8;
                    }
                }
            }

            AuthorizationCodeRequestUrl authorizationUrl = this.flow.newAuthorizationUrl();
            authorizationUrl.setRedirectUri(this.getRedirectUri(req));
            this.onAuthorization(req, resp, authorizationUrl);
            this.credential = null;
        } finally {
            this.lock.unlock();
        }

    }

    protected abstract AuthorizationCodeFlow initializeFlow() throws ServletException, IOException;

    protected abstract String getRedirectUri(HttpServletRequest var1) throws ServletException, IOException;

    protected abstract String getUserId(HttpServletRequest var1) throws ServletException, IOException;

    protected final Credential getCredential() {
        return this.credential;
    }

    protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException {
        resp.sendRedirect(authorizationUrl.build());
    }
}
