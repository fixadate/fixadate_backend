package com.fixadate.global.auth.dto.request;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import com.fixadate.global.oauth.entity.OAuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRegistRequestDto(
        @NotBlank String oauthId,
        @NotBlank String oauthPlatform,
        @NotBlank String name,
        @NotBlank String profileImg,
        @NotBlank String nickname,
        @NotNull Integer birth,
        @NotBlank String gender,
        @NotBlank String profession,
        @NotBlank String signatureColor
) {

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

    public static MemberRegistRequestDto create(
            String oauthId,
            String oauthPlatform,
            String name,
            String profileImg,
            String nickname,
            Integer birth,
            String gender,
            String profession,
            String signatureColor
    ) {
        return new MemberRegistRequestDto(
                oauthId,
                oauthPlatform,
                name,
                profileImg,
                nickname,
                birth,
                gender,
                profession,
                signatureColor
        );
    }
}
