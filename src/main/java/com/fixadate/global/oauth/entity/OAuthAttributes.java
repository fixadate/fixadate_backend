package com.fixadate.global.oauth.entity;

import static com.fixadate.global.oauth.ConstantValue.*;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.oauth.ConstantValue;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public class OAuthAttributes {
    private Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private String oauthId;
    private String nameAttributesKey;
    private String email;
    private String gender;
    private int age;
    private String birthday;
    private OAuthProvider oAuthProvider;
    private String profileImage;
    private String nickname;

    @Builder
    public OAuthAttributes(String oauthId, Map<String, Object> attributes, String nameAttributesKey,
                           String email, String gender, int age, String birthday, OAuthProvider oAuthProvider,
                           String profileImage, String nickname) {
        this.gender = gender;
        this.age = age;
        this.birthday = birthday;
        this.oauthId = oauthId;
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.email = email;
        this.oAuthProvider = oAuthProvider;
        this.profileImage = profileImage;
        this.nickname = nickname;
    }

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        } else if ("google".equals(socialName)) {
            return ofGoogle("sub", attributes);
        }
        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .oauthId(String.valueOf(attributes.get(GOOGLE_OAUTH_ID)))
                .email(String.valueOf(attributes.get(EMAIL)))
                .oAuthProvider(OAuthProvider.Google)
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        String oauthId = String.valueOf(attributes.get(KAKAO_NAVER_OAUTH_ID));
        log.info(attributes.toString());

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .oauthId(oauthId)
                .nickname(String.valueOf(kakaoProfile.get(NICK_NAME)))
                .gender(String.valueOf(kakaoAccount.get("gender")))
                .birthday(String.valueOf(kakaoAccount.get("birthday")))
                .profileImage(String.valueOf(kakaoAccount.get("profile_image")))
                .email(String.valueOf(kakaoAccount.get(EMAIL)))
                .oAuthProvider(OAuthProvider.Kakao)
                .nameAttributesKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    public Member toEntity() {
        /*
        todo : oauth로그인 할 때 nickname, email, OAuthId를 얻어오는데 profileImg도 얻어오도록 구현하기.
         */
        return Member.builder()
                .birth(Integer.parseInt(birthday))
                .nickname(nickname)
                .gender(gender)
                .oauthPlatform(oAuthProvider)
                .oauthId(oauthId)
                .oauthPlatform(oAuthProvider)
                .build();
    }
}