package com.example.videouploader.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class S3OperationsService {
    private final S3Template s3Template;

    public URL generateSignedPutUrl(String bucketName, String key) {
        return s3Template.createSignedPutURL(bucketName, key, Duration.ofMinutes(30));
    }
}
