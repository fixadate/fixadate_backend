package com.fixadate.domain.googleCalendar.repository;

import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleRepository extends JpaRepository<GoogleCredentials, String> {
    Optional<GoogleCredentials> findGoogleCredentialsByChannelId(String channelId);
}
