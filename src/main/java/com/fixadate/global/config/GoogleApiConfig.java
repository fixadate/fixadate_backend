package com.fixadate.global.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Configuration
public class GoogleApiConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${google.api.key}")
    private String apiKey;
    static final String ACCESS_TYPE = "offline";
    static final String USER_ID = "user";
    static final String REQUEST_HEADER = "Bearer ";
    static final String FIELD_NAME = "key";
    static final String APPLICATION_NAME = "fixadate";
    static final String CHANNEL_TYPE = "web_hook";
    static final String BASE_URL = "https://api/fixadate.app";
    static final String NOTIFICATION_URL = "/notifications";



    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JacksonFactory.getDefaultInstance(),
                clientId, clientSecret, Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType(ACCESS_TYPE)
                .build();
    }

    @Bean
    public Calendar calendarService(GoogleAuthorizationCodeFlow flow) throws GeneralSecurityException, IOException {
        HttpRequestInitializer initializer = new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                Credential credential = flow.loadCredential(USER_ID);
                if (credential != null) {
                    request.getHeaders().setAuthorization(REQUEST_HEADER + credential.getAccessToken());
                    GenericUrl url = request.getUrl();
                    url.put(FIELD_NAME, apiKey);
                    request.setUrl(url);
                }
            }
        };

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), initializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Channel createChannel() {
        return new Channel()
                .setId(UUID.randomUUID().toString())
                .setType(CHANNEL_TYPE)
                .setAddress(BASE_URL + NOTIFICATION_URL)
                .setExpiration(System.currentTimeMillis() + 9_000_000_000_000L)
                .setToken("tokenValue");
    }
}
