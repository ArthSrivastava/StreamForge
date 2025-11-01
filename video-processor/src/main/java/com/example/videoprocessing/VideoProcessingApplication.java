package com.example.videoprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO: Build FFMPEG commands for HLS output
@SpringBootApplication
public class VideoProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoProcessingApplication.class, args);
	}

}
