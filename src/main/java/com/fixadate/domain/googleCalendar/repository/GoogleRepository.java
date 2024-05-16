package com.fixadate.domain.googleCalendar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.member.entity.Member;

public interface GoogleRepository extends JpaRepository<GoogleCredentials, String> {
	Optional<GoogleCredentials> findGoogleCredentialsByChannelId(String channelId);

	Optional<GoogleCredentials> findGoogleCredentialsByMember(Member member);
}
