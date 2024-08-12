package com.fixadate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@TestConfiguration
public class LocalStackContainerProvider {
	private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("localstack/localstack:0.12.18");

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		return new LocalStackContainer(DOCKER_IMAGE_NAME)
			.withServices(LocalStackContainer.Service.S3);
	}

	@Bean
	public AwsBasicCredentials basicAwsCredentials(LocalStackContainer localStackContainer) {
		return AwsBasicCredentials.create(
			localStackContainer.getAccessKey(),
			localStackContainer.getSecretKey()
		);
	}

	@Bean
	public S3Client s3Client(LocalStackContainer localStackContainer) {
		S3Client s3Client = S3Client.builder()
									.region(Region.US_EAST_1)
									.endpointOverride(
										localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3)
									)
									.credentialsProvider(
										StaticCredentialsProvider.create(
											basicAwsCredentials(localStackContainer))
									)
									.build();

		s3Client.createBucket(builder -> builder.bucket(bucket));
		return s3Client;
	}

	@Bean
	public S3Presigner s3Presigner(LocalStackContainer localStackContainer) {
		return S3Presigner.builder()
						  .region(Region.US_EAST_1)
						  .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
						  .credentialsProvider(
							  StaticCredentialsProvider.create(
								  basicAwsCredentials(localStackContainer))
						  )
						  .build();
	}
}
