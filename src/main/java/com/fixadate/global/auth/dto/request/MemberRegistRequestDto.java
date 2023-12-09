package com.fixadate.global.auth.dto.request;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import com.fixadate.global.oauth.entity.OAuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegistRequestDto {
    @NotBlank
    private String oauthId;
    @NotBlank
    private String oauthPlatform;
    @NotBlank
    private String name;
    @NotBlank
    private String profileImg;
    @NotBlank
    private String nickname;
    @NotNull
    private Integer birth;
    @NotBlank
    private String gender; //boolean to selection
    @NotBlank
    private String profession;
    @NotBlank
    private String signatureColor;

    public Member of() {
        return Member.builder()
                .oauthId(oauthId)
                .oauthPlatform(getOAuthProviderFromString(oauthPlatform))
                .name(name)
                .profileImg(profileImg)
                .nickname(nickname)
                .birth(birth)
                .gender(gender)
                .profession(profession)
                .signatureColor(signatureColor)
                .build();
    }

    private OAuthProvider getOAuthProviderFromString(String oauthPlatform) {
        return switch (oauthPlatform.toLowerCase()) {
            case "kakao" -> OAuthProvider.Kakao;
            case "google" -> OAuthProvider.Google;
            case "apple" -> OAuthProvider.Apple;
            default -> throw new UnknownOAuthPlatformException();
        };
    }
}
