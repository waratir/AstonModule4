package com.module4.kafka;

import com.module4.dto.event.UserEvent;
import com.module4.messaging.UserEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserEventProducerIntegrationTest {

    @Container
    @ServiceConnection
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka-native:3.8.0")
    );

    @Autowired
    private UserEventProducer producer;

    private final BlockingQueue<UserEvent> results = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "user-events", groupId = "test-group")
    void listen(UserEvent event) {
        log.info("Received event: {}", event);
        results.add(event);
    }

    @Test
    void testSendEvent() {
        UserEvent event = new UserEvent("test@mail.com", UserEvent.OperationType.CREATED);

        producer.sendUserEvent(event);

        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    UserEvent received = results.poll();
                    assertThat(received).isNotNull();
                    assertThat(received.getEmail()).isEqualTo("test@mail.com");
                    assertThat(received.getOperationType()).isEqualTo(UserEvent.OperationType.CREATED);
                });
    }
}