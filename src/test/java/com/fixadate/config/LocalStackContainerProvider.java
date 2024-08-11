package com.fixadate.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class LocalStackContainerProvider {

	private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack");

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		return new LocalStackContainer(LOCALSTACK_IMAGE)
			.withServices(LocalStackContainer.Service.S3);
	}

	@Bean
	public AmazonS3Client amazonS3Client(LocalStackContainer localStackContainer) {
		return (AmazonS3Client) AmazonS3ClientBuilder.standard()
													 .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(
														 LocalStackContainer.Service.S3))
													 .withCredentials(localStackContainer.getDefaultCredentialsProvider())
													 .build();
	}

	@Bean
	public S3Client s3Client(LocalStackContainer localStackContainer) {
		return S3Client.builder()
					   .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
					   .credentialsProvider(
						   StaticCredentialsProvider.create(
							   AwsBasicCredentials.create(
								   localStackContainer.getAccessKey(), localStackContainer.getSecretKey())))
					   .region(Region.of(localStackContainer.getRegion()))
					   .build();
	}
}
