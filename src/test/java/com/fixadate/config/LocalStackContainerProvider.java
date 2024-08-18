package com.fixadate.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static software.amazon.awssdk.regions.Region.US_EAST_1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class LocalStackContainerProvider {

	private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("localstack/localstack:0.12.18");

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		return new LocalStackContainer(DOCKER_IMAGE_NAME).withServices(S3);
	}

	@Bean
	public AwsBasicCredentials basicAwsCredentials(final LocalStackContainer localStackContainer) {
		return AwsBasicCredentials.create(
			localStackContainer.getAccessKey(),
			localStackContainer.getSecretKey()
		);
	}

	@Bean
	public S3Client s3Client(final LocalStackContainer localStackContainer) {
		S3Client s3Client = S3Client.builder()
									.region(US_EAST_1)
									.endpointOverride(
										localStackContainer.getEndpointOverride(S3)
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
	public S3Presigner s3Presigner(final LocalStackContainer localStackContainer) {
		return S3Presigner.builder()
						  .region(US_EAST_1)
						  .endpointOverride(localStackContainer.getEndpointOverride(S3))
						  .credentialsProvider(
							  StaticCredentialsProvider.create(
								  basicAwsCredentials(localStackContainer))
						  )
						  .build();
	}
}
