package com.fixadate.domain.member.dto.response;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 2.
 */
public record MemberInfoResponse(
	String name,
	String nickname,
	String birth,
	String gender,
	String signatureColor,
	String profession,
	String url) {
}
