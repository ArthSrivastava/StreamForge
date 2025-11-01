package com.example.videojobconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;

@Configuration
class ECSConfig {

    @Bean
    EcsClient ecsClient() {
        return EcsClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }
}
