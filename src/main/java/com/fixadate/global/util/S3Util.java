package com.fixadate.global.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Util {
	private final S3Presigner s3Presigner;

	@Value("${cloud.aws.bucket-name}")
	private String bucketName;

	//upload
	public String generatePresignedUrlForUpload(String fileName, String contentType) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(5))
			.putObjectRequest(putObjectRequest)
			.build();
		return s3Presigner.presignPutObject(presignRequest).url().toString();
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

	//delete
	public String generatePresignedUrlForDelete(String fileName) {
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();

		DeleteObjectPresignRequest deleteRequest = DeleteObjectPresignRequest.builder()
			.deleteObjectRequest(deleteObjectRequest)
			.signatureDuration(Duration.ofMinutes(5))
			.build();

		return s3Presigner.presignDeleteObject(deleteRequest).url().toString();
	}
}
