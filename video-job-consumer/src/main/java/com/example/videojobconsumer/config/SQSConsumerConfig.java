package com.example.videojobconsumer.config;

import com.example.videojobconsumer.VideoJobPayload;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.operations.TemplateAcknowledgementMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
class SQSConsumerConfig {

    @Bean
    SqsTemplate sqsTemplate() {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient())
                .configure(options -> options
                        .acknowledgementMode(TemplateAcknowledgementMode.MANUAL)
                        .queueNotFoundStrategy(QueueNotFoundStrategy.FAIL)
                )
                .build();
    }

    @Bean
    SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    @Bean
    SqsMessageListenerContainerFactory<VideoJobPayload> sqsMessageListenerContainerFactory() {
        return SqsMessageListenerContainerFactory.
                <VideoJobPayload>builder()
                .sqsAsyncClient(sqsAsyncClient())
                .build();
    }
}
