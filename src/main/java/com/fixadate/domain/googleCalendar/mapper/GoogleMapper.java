package com.fixadate.domain.googleCalendar.mapper;

import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.model.Channel;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 1.
 */
public class GoogleMapper {
	private GoogleMapper() {
	}

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

	public static Channel toChannel(String channelId, String resourceId) {
		Channel channel = new Channel();
		channel.setId(channelId);
		channel.setResourceId(resourceId);
		return channel;
	}

}
