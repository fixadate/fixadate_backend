package com.fixadate.global.config;

import com.fixadate.domain.adate.exception.GoogleCalendarWatchException;
import com.fixadate.domain.adate.exception.GoogleClientSecretsException;
import com.fixadate.domain.adate.exception.GoogleCredentialException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import static com.fixadate.global.oauth.ConstantValue.*;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_EVENTS_READONLY;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_READONLY;

@Configuration
@Slf4j
public class GoogleApiConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static final List<String> SCOPES = List.of(CALENDAR_READONLY, CALENDAR_EVENTS_READONLY);
    private static HttpTransport HTTP_TRANSPORT;
    private static GoogleAuthorizationCodeFlow flow;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = getClientSecrets();
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKEN_DIRECTORY_PATH)))
                .setAccessType(ACCESS_TYPE)
                .setApprovalPrompt(APPROVAL_PROMPT)
                .build();
    }


    public static String getClientId(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getSession().getId();
    }

    public static GoogleAuthorizationCodeFlow initializeFlow() {
        return flow;
    }


    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
        requestUrl.setRawPath(GOOGLE_CALLBACK);
        return requestUrl.build();
    }

    private static GoogleClientSecrets getClientSecrets(){
        InputStream in = GoogleApiConfig.class.getResourceAsStream(FILE_PATH);
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            clientSecrets.getDetails().setClientId(CLIENT_ID);
            clientSecrets.getDetails().setClientSecret(CLIENT_SECRET);
            return clientSecrets;
        } catch (IOException e) {
            throw new GoogleClientSecretsException();
        }
    }

    public Channel executeWatchRequest(String accessToken) {
        try {
            Channel channel = createChannel();
            Calendar.Events.Watch watch = calendarService(accessToken)
                    .events()
                    .watch(CALENDAR_ID, channel);
            return watch.execute();
        } catch (IOException e) {
            throw new GoogleCalendarWatchException();
        }
    }

    public Calendar calendarService(String accessToken) {
        Credential credential = getCredentials(accessToken);
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Credential getCredentials(String accessToken) {
        try {
            log.info(accessToken);
            return flow.loadCredential(accessToken);
        } catch (IOException e) {
            throw new GoogleCredentialException();
        }
    }

    private Channel createChannel() {
        String tokenValue = UUID.randomUUID().toString();
        return new Channel()
                .setId(tokenValue)
                .setType(CHANNEL_TYPE)
                .setAddress(BASE_URL + NOTIFICATION_URL)
                .setExpiration(System.currentTimeMillis() + 9_000_000_000L)
                .setToken("tokenValue");
    }
}
