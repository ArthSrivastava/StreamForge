package com.example.videojobconsumer;

import com.example.videojobconsumer.config.ApplicationProps;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoJobConsumer {

    private final ApplicationProps applicationProps;

    private final EcsClient ecsClient;

    /**
     * Listens to the video uploaded task on the SQS queue and starts up an ECS task
     *
     * @param payload         {@link  VideoJobPayload} containing the path of the uploaded raw video
     * @param acknowledgement used to manually acknowledge the SQS message
     */
    @SqsListener(
            queueNames = {"${job.sqs-queue-name}"},
            factory = "sqsMessageListenerContainerFactory"
    )
    public void processJob(
            List<Message<VideoJobPayload>> payload,
            Acknowledgement acknowledgement
    ) {
        log.info(
                "Received job payload from SQS: {}",
                payload
        );

        try {
            for (Message<VideoJobPayload> message : payload) {
                VideoJobPayload videoJobPayload = message.getPayload();
                String videoS3Path = videoJobPayload.rawVideoS3Path();

                RunTaskRequest videoEncoderTask = buildVideoEncoderTask(videoS3Path);

                RunTaskResponse runTaskResponse = ecsClient.runTask(videoEncoderTask);

                log.info(
                        "ECS task response: {}",
                        runTaskResponse
                );
            }
        } finally {
            acknowledgement.acknowledge();
        }

    }

    private static RunTaskRequest buildVideoEncoderTask(String videoS3Path) {
        TaskOverride taskOverride = TaskOverride
                .builder()
                .containerOverrides(
                        ContainerOverride
                                .builder()
                                .environment(
                                        KeyValuePair.builder()
                                                .name("s3-video-path")
                                                .value(videoS3Path)
                                                .build()
                                )
                                .build()
                )
                .build();

        NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
                .awsvpcConfiguration(
                        AwsVpcConfiguration.builder()
                                .subnets("")
                                .securityGroups("")
                                .assignPublicIp(AssignPublicIp.ENABLED)
                                .build())
                .build();

        return RunTaskRequest.builder()
                .cluster("")
                .taskDefinition("")
                .launchType(LaunchType.FARGATE)
                .count(1)
                .networkConfiguration(networkConfiguration)
                .overrides(taskOverride)
                .build();
    }

}
