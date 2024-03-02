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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleApiConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${google.api.key}")
    private String apiKey;

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JacksonFactory.getDefaultInstance(),
                clientId, clientSecret, Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline")
                .build();
    }

    @Bean
    public Calendar calendarService(GoogleAuthorizationCodeFlow flow) throws GeneralSecurityException, IOException {
        HttpRequestInitializer initializer = new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                Credential credential = flow.loadCredential("user");
                if (credential != null) {
                    request.getHeaders().setAuthorization("Bearer " + credential.getAccessToken());
                    GenericUrl url = request.getUrl();
                    url.put("key", apiKey);
                    request.setUrl(url);
                }
            }
        };

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), initializer)
                .setApplicationName("fixadate")
                .build();
    }
}
