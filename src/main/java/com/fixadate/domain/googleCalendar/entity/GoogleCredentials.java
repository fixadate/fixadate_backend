package com.fixadate.domain.googleCalendar.entity;

import com.fixadate.domain.member.entity.Member;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.model.Channel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelId;
    private String accessToken;
    private String refreshToken;
    private String resourceId;
    private String resourceUri;
    private String resourceState;
    private Long channelExpiration;
    private String channelToken;
    private String userId;

    @OneToOne(mappedBy = "googleCredentials")
    private Member member;

    public static GoogleCredentials getGoogleCredentialsFromCredentials(Channel channel, String userId,
                                                                        TokenResponse tokenResponse) {
        return GoogleCredentials.builder()
                .channelId(channel.getId())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .resourceId(channel.getResourceId())
                .resourceUri(channel.getResourceUri())
                .resourceState(channel.getType())
                .channelExpiration(channel.getExpiration())
                .channelToken(channel.getToken())
                .userId(userId)
                .build();
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
