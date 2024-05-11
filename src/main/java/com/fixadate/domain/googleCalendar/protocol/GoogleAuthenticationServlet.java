package com.fixadate.domain.googleCalendar.protocol;

import com.fixadate.global.util.GoogleUtil;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
// 컴파일러의 경고를 무시하겠다고 하는 것임
@WebServlet(urlPatterns = "/oauth2Login")
public class GoogleAuthenticationServlet extends AbstractAuthorizationCodeServlet {
	@Override
	protected AuthorizationCodeFlow initializeFlow() {
		return GoogleUtil.initializeFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest) {
		return GoogleUtil.getRedirectUri(httpServletRequest);
	}

	@Override
	protected String getUserId(HttpServletRequest httpServletRequest) {
		return GoogleUtil.getClientId(httpServletRequest);
	}
}
