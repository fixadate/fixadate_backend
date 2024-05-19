package com.fixadate.domain.googleCalendar.protocol;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractAuthorizationCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Lock lock = new ReentrantLock();
	private Credential credential;
	private AuthorizationCodeFlow flow;

	protected AbstractAuthorizationCodeServlet() {
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String memberId = req.getHeader(ID.getValue());
		if (memberId == null || memberId.isBlank()) {
			resp.sendError(HttpStatus.SC_BAD_REQUEST, NOT_FOUND_MEMBER_ID_IN_HEADER.getMessage());
			return;
		}

		try (Connection connection = DatabaseConnection.initializeDatabase();
			 PreparedStatement st = connection.prepareStatement(
				 "SELECT id FROM member  WHERE id = ?")) {
			st.setString(1, memberId);
			ResultSet resultSet = st.executeQuery();
			if (!resultSet.next()) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, NOT_FOUND_MEMBER_ID.getMessage());
				return;
			}
		} catch (Exception e) {
			resp.sendError(500, INVALID_SQL.getMessage());
			return;
		}

		this.lock.lock();

		try {
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

	protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp,
		AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException {
		resp.sendRedirect(authorizationUrl.build());
	}
}
