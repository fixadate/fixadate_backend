package com.fixadate.global.s3.service;

import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    //upload
    public String generatePresignedUrlForUpload(String fileName, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
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

    //download
    public String generatePresignedUrlForDownload(String fileName) {
        GetObjectRequest getObjectPresignRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectPresignRequest)
                .signatureDuration(Duration.ofMinutes(5))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
