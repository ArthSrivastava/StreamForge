package com.example.videouploader.video;

import com.example.videouploader.s3.S3OperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.UUID;

import static com.example.videouploader.common.AppConstants.S3_BUCKET_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
class VideoUploaderService {
    private final S3OperationsService s3OperationsService;

    VideoUploadResponse processVideoUpload(VideoUploadRequest videoUploadRequest) {
        String vidName = videoUploadRequest.name();
        String key = UUID.randomUUID().toString().concat("-").concat(vidName);
        log.info("Generated Key: [{}] for video: [{}]", key, vidName);

        //TODO: persist the metadata to a database

        //generate a signed url
        URL signedUrl = s3OperationsService.generateSignedPutUrl(S3_BUCKET_NAME, key);
        return new VideoUploadResponse(signedUrl);
    }
}
