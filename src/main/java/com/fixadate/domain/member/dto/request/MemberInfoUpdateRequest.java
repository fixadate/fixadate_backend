package com.fixadate.domain.member.dto.request;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 2.
 */
public record MemberInfoUpdateRequest(
	String nickname,
	String signatureColor,
	String profession,
	String profileImg
) {
}
