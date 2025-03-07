package com.fixadate.domain.notification.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.FcmMessage.Notification;
import com.fixadate.domain.notification.service.NotificationService;
import com.fixadate.domain.pushkey.entity.PushKey;
import com.fixadate.domain.pushkey.repository.PushKeyRepository;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseCloudMessageService {

    @Value("${firebase.service-key}")
    String firebaseConfigPath;

    @Value("${firebase.api-url}")
    String API_URL;

    private final String PLATFORM_URL = "https://www.googleapis.com/auth/cloud-platform";

    private final String UTF_8 = "application/json;charset=utf-8";

    ObjectMapper objectMapper = new ObjectMapper();
    OkHttpClient httpClient = new OkHttpClient();

    private final PushKeyRepository pushKeyRepository;
    private final NotificationService notificationService;

    public Optional<PushKey> findPushKey(Member member) {
        return pushKeyRepository.findByMemberId(member.getId());
    }

    public boolean existPushKeyByMember(Member member) {
        return pushKeyRepository.existsByMemberId(member.getId());
    }

    public List<PushKey> findAllByPushKey(String PushKey) {
        return pushKeyRepository.findAllByPushKey(PushKey);
    }

    public boolean equalPushKey(Member member, String PushKey) {
        PushKey foundPushKey = pushKeyRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new IllegalArgumentException("해당 멤버의 push key가 존재하지 않습니다.")
        );
        return foundPushKey.getPushKey().equals(PushKey);
    }

    public void savePushKey(Member member, String newPushKey) {
        pushKeyRepository.save(new PushKey(member.getId(), newPushKey));
    }

    public void updatePushKey(Member member, String newPushKey) {
        PushKey foundPushKey = pushKeyRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new IllegalArgumentException("해당 멤버의 push key가 존재하지 않습니다.")
        );
        foundPushKey.updatePushKey(newPushKey);
        pushKeyRepository.save(foundPushKey);
    }

    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
            .createScoped(List.of(PLATFORM_URL));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }


    // targetToken은 app으로 받을 대상의 토큰
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);
        RequestBody requestBody = RequestBody.create(message,
            MediaType.get(UTF_8));
        Request request = new Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
            .addHeader(HttpHeaders.CONTENT_TYPE, UTF_8)
            .build();
        Response response = httpClient.newCall(request).execute();
        log.info(response.body().string());

        // 실시간 알림 체크 확인
        PushKey pushKey = pushKeyRepository.findPushKeyByPushKey(targetToken).orElse(null);
        if(pushKey == null){
            throw new RuntimeException("");
        }
        notificationService.sendEvent(pushKey.getMemberId());
    }

    public void sendMessageToWithData(String targetToken, String title, String body,
        Map<String, Object> data) throws IOException {
        String message = makeMessageWithData(targetToken, title, body, data);
        log.info("fcm msg: " + message);
        RequestBody requestBody = RequestBody.create(message,
            MediaType.get(UTF_8));
        Request request = new Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
            .addHeader(HttpHeaders.CONTENT_TYPE, UTF_8)
            .build();
        Response response = httpClient.newCall(request).execute();
        log.info(response.body().string());

        // 실시간 알림 체크 확인
        PushKey pushKey = pushKeyRepository.findPushKeyByPushKey(targetToken).orElse(null);
        if(pushKey == null){
            throw new RuntimeException("");
        }
        notificationService.sendEvent(pushKey.getMemberId());
    }

    private String makeMessage(String targetToken, String title, String body) throws
        JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
            .message(FcmMessage.Message.builder()
                .token(targetToken)
                .notification(FcmMessage.Notification.builder()
                    .title(title)
                    .body(body)
                    .image(null)
                    .build()
                )
                .build()
            )
            .validate_only(false)
            .build();
        // FcmMsg를 만들고 objectMapper를 통해 String 으로 변환
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String makeMessageWithData(String targetToken, String title, String body,
        Map<String, Object> map) throws
        JsonProcessingException {
        map.put("title", title);
        map.put("body", body);

        FcmMessageWithData fcmMessage = FcmMessageWithData.builder()
            .message(
                FcmMessageWithData.Message.builder()
                    .data(map)
                    .token(targetToken)
                    .notification(FcmMessageWithData.Notification.builder()
                        .title(title)
                        .body(body)
                        .image((String) map.get("image"))
                        .build())
                    .build()
            )
            .build();
        // FcmMsg를 만들고 objectMapper를 통해 String 으로 변환
        return objectMapper.writeValueAsString(fcmMessage);
    }

}