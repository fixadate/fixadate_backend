package com.fixadate.domain.googleCalendar.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.services.calendar.model.Channel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String channelId;
    private String accessToken;
    String resourceId;
    String resourceUri;
    String resourceState;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long channelExpiration;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String channelToken;
    String userId;

    public static GoogleCredentials getGoogleCredentialsFromCredentials(Channel channel, String accessToken,
                                                                        String userId) {
        return GoogleCredentials.builder()
                .channelId(channel.getId())
                .accessToken(accessToken)
                .resourceId(channel.getResourceId())
                .resourceUri(channel.getResourceUri())
                .resourceState(channel.getType())
                .channelExpiration(channel.getExpiration())
                .channelToken(channel.getToken())
                .userId(userId)
                .build();
    }
}
