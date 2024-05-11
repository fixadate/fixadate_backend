package com.fixadate.global.util;

import com.fixadate.domain.googleCalendar.exception.GoogleCalendarWatchException;
import com.fixadate.domain.googleCalendar.exception.GoogleClientSecretsException;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialException;
import com.fixadate.domain.googleCalendar.exception.QueryMissingException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import static com.fixadate.global.util.constant.ConstantValue.*;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_EVENTS_READONLY;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_READONLY;

@Configuration
public class GoogleUtil {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private Channel channel;
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static final List<String> SCOPES = List.of(CALENDAR_READONLY, CALENDAR_EVENTS_READONLY);
    private static HttpTransport HTTP_TRANSPORT;
    private static GoogleAuthorizationCodeFlow flow;
    private static FileDataStoreFactory dataStoreFactory;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private DataStore<String> syncSettingsDataStore;
    public static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/token");


    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        GoogleClientSecrets clientSecrets = getClientSecrets();
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType(ACCESS_TYPE.getValue())
                .setApprovalPrompt(APPROVAL_PROMPT.getValue())
                .build();
        channel = createChannel();
        syncSettingsDataStore = GoogleUtil.getDataStoreFactory().getDataStore(SYNC_SETTINGS.getValue());
    }


    public static String getClientId(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getSession(true).getId();
    }

    public static GoogleAuthorizationCodeFlow initializeFlow() {
        return flow;
    }


    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
        requestUrl.setRawPath(GOOGLE_CALLBACK.getValue());
        return requestUrl.build();
    }

    public static String getUserId(HttpServletRequest request) {
        String id = request.getHeader(ID.getValue());
        if (id != null) {
            return id;
        } else {
            throw new QueryMissingException();
        }
    }

    private static GoogleClientSecrets getClientSecrets(){
        InputStream in = GoogleUtil.class.getResourceAsStream(FILE_PATH.getValue());
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            clientSecrets.getDetails().setClientId(CLIENT_ID);
            clientSecrets.getDetails().setClientSecret(CLIENT_SECRET);
            return clientSecrets;
        } catch (IOException e) {
            throw new GoogleClientSecretsException();
        }
    }

    public Channel executeWatchRequest(String userId) {
        try {
            Calendar.Events.Watch watch = calendarService(userId)
                    .events()
                    .watch(CALENDAR_ID.getValue(), channel);
            return watch.execute();
        } catch (IOException e) {
            throw new GoogleCalendarWatchException();
        }
    }

    public Calendar calendarService(String userId) {
        Credential credential = getCredentials(userId);
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME.getValue())
                .build();
    }

    public static Credential getCredentials(String userId) {
        try {
            return flow.loadCredential(userId);
        } catch (IOException e) {
            throw new GoogleCredentialException();
        }
    }

    public void registCredential(TokenResponse tokenResponse, String userId) {
        try {
            flow.createAndStoreCredential(tokenResponse, userId);
        } catch (IOException e) {
            throw new GoogleCredentialException();
        }
    }

    private Channel createChannel() {
        String tokenValue = UUID.randomUUID().toString();
        return new Channel()
                .setId(tokenValue)
                .setType(CHANNEL_TYPE.getValue())
                .setAddress(BASE_URL.getValue() + NOTIFICATION_URL.getValue())
                .setExpiration(System.currentTimeMillis() + 9_000_000_000L)
                .setToken("tokenValue");
    }

    public static DataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    public DataStore<String> getSyncSettingsDataStore() {
        return syncSettingsDataStore;
    }
}
