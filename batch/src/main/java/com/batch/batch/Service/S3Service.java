package com.batch.batch.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.net.URI;

@Service
public class S3Service {
    private S3Client s3Client;

    @Value("${key.s3.secret}")
    private String SECRET_KEY;
    @Value("${key.s3.access}")
    private String ACCESS_KEY;
    @Value("${others.host}")
    private String HOST;

    @PostConstruct
    public void init() {
        String endpoint = "http://" + HOST + ":9000";

        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }


    public boolean checkFolderExists(String bucket, String prefix) {
        String fullPrefix = prefix.endsWith("/") ? prefix : prefix + "/";
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(fullPrefix)
                .maxKeys(1)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return !response.contents().isEmpty();
    }
}
