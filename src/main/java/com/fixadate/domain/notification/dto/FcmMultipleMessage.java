package com.fixadate.domain.notification.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMultipleMessage {

    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {

        private Notification notification;
        private List<String> registration_ids;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {

        private String title;
        private String body;
        private String image;
    }

}