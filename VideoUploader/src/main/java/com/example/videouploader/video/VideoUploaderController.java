package com.example.videouploader.video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
class VideoUploaderController {
    private final VideoUploaderService videoUploaderService;

    @PostMapping
    ResponseEntity<VideoUploadResponse> uploadVideo(@RequestBody VideoUploadRequest videoUploadRequest) {
        log.info("Video upload request: {}", videoUploadRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(videoUploaderService.processVideoUpload(videoUploadRequest));
    }
}
