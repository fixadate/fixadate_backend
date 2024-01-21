package com.fixadate.global.auth.service;


import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final S3Presigner s3Presigner;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    public Optional<Member> findMemberByOAuthId(String oauthId) {
        return memberRepository.findMemberByOauthId(oauthId);
    }

    @Transactional
    public void registMember(MemberRegistRequestDto memberRegistRequestDto) {
        Member member = memberRegistRequestDto.of();
        memberRepository.save(member);
    }

    public String getPresignedUrlFromRequest(MemberRegistRequestDto memberRegistRequestDto) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(memberRegistRequestDto.profileImg())
                .contentType(memberRegistRequestDto.contentType())
                .build();

        PutObjectPresignRequest presignRequest = getPresignRequest(putObjectRequest);

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private PutObjectPresignRequest getPresignRequest(PutObjectRequest putObjectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();
    }

}
