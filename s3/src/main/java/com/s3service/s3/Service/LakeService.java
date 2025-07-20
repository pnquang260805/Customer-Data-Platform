package com.s3service.s3.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.net.URI;

@Service
@Slf4j
public class LakeService {
    S3Client s3Client;

    public LakeService(@Value("${s3.secret}") String secret, @Value("${s3.access}") String access, @Value("${others.host}") String host) {
        log.info("Access: {}", access);
        log.info("Secret: {}", secret);
        log.info("Host: {}", host);

        String endpoint = "http://" + host + ":9000";
        AwsCredentials credentials = AwsBasicCredentials.create(access, secret);

        this.s3Client = S3Client.builder()
                .region(Region.AP_EAST_1)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public boolean bucketExisted(String bucketName){
        try{
            s3Client.headBucket(request -> request.bucket(bucketName));
            return true;
        } catch (NoSuchBucketException e){
            log.info("{} existed", bucketName);
            return false;
        }
    }

    public void createBucket(String bucketName){
        s3Client.createBucket(request->request.bucket(bucketName));
        log.info("{} has been created", bucketName);
    }

    public boolean folderExisted(String bucketName, String pathToFolder){
        String fullPath = pathToFolder.endsWith("/") ? pathToFolder : pathToFolder + "/";
        ListObjectsV2Request request = ListObjectsV2Request
                .builder()
                .bucket(bucketName)
                .prefix(fullPath)
                .maxKeys(1)
                .build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return !response.contents().isEmpty();
    }
}
