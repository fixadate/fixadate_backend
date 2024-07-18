package com.fixadate.domain.googlecalendar.protocol;

import static com.fixadate.global.util.constant.ConstantValue.*;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.fixadate.global.util.GoogleUtil;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/oauth2callback")
public class Oauth2CallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
		throws IOException {
		String memberId = req.getHeader(ID.getValue());
		String userId = req.getSession().getId();
		ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN.getValue(), credential.getAccessToken())
			.httpOnly(true)
			.build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN.getValue(), credential.getRefreshToken())
			.httpOnly(true)
			.build();
		resp.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		resp.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
		resp.sendRedirect("/v1/google/watch?userId=" + userId + "&memberId=" + memberId);
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
		return GoogleUtil.initializeFlow();

	}

	@Override
	protected String getRedirectUri(HttpServletRequest var1) {
		return GoogleUtil.getRedirectUri(var1);
	}

	@Override
	protected String getUserId(HttpServletRequest var1) {
		return GoogleUtil.getClientId(var1);
	}
}
