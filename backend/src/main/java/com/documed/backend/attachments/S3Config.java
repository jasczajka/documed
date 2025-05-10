package com.documed.backend.attachments;

import java.time.Duration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Data
@Configuration
public class S3Config {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.s3.access_key_id}")
  private String accessKeyId;

  @Value("${aws.s3.secret_access_key}")
  private String secretAccessKey;

  @Value("${aws.s3.bucket_name}")
  private String bucketName;

  private Duration presignedPostExpiration = Duration.ofHours(1);
  private Duration presignedGetExpiration = Duration.ofDays(1);

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .region(Region.of(region))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .region(Region.of(region))
        .build();
  }
}
