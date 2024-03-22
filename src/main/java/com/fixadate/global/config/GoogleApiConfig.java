package com.fixadate.global.config;

import com.fixadate.domain.adate.exception.GoogleCalendarWatchException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import jakarta.annotation.PostConstruct;
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

import static com.google.api.services.calendar.CalendarScopes.CALENDAR_EVENTS_READONLY;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_READONLY;

@Configuration
@Slf4j
public class GoogleApiConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${google.api.key}")
    private String apiKey;
    static final String ACCESS_TYPE = "offline";
    static final String APPLICATION_NAME = "fixadate";
    static final String CHANNEL_TYPE = "web_hook";
    static final String BASE_URL = "https://api/fixadate.app";
    static final String NOTIFICATION_URL = "/google/notifications";
    static final String FILE_PATH = "/credentials.json";
    static final String TOKEN_DIRECTORY_PATH = "tokens";
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HttpTransport HTTP_TRANSPORT;
    private GoogleAuthorizationCodeFlow flow;

    //userId
    public Calendar calendarService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @PostConstruct
    void initializeFlow() throws GeneralSecurityException, IOException {
        if (clientId == null || clientSecret == null) {
            return;
        }
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    }

    public Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = GoogleApiConfig.class.getResourceAsStream(FILE_PATH);
        // try with resources 공부하고 적용해보기
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                List.of(CALENDAR_READONLY, CALENDAR_EVENTS_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKEN_DIRECTORY_PATH)))
                .setAccessType(ACCESS_TYPE)
                .setApprovalPrompt("force")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    public Channel executeWatchRequest() {
        try {
            Channel channel = createChannel();
            Calendar.Events.Watch watch = calendarService()
                    .events()
                    .watch("primary", channel);
            return watch.execute();
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new GoogleCalendarWatchException();
        } catch (GeneralSecurityException e) {
            throw new GoogleCalendarWatchException();
        }
    }
    private Channel createChannel() {
        return new Channel()
                .setId(UUID.randomUUID().toString())
                .setType(CHANNEL_TYPE)
                .setAddress(BASE_URL + NOTIFICATION_URL)
                .setExpiration(System.currentTimeMillis() + 9_000_000_000L)
                .setToken("tokenValue");
    }
}
