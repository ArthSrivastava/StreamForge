package com.example.videojobconsumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "job")
@Data
public class ApplicationProps {
    private String sqsQueueName;
}

