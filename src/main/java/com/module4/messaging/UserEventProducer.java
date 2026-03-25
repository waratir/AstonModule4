package com.module4.messaging;

import com.module4.dto.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.user-events:user-events}")
    private String userEventsTopic;

    public void sendUserEvent(UserEvent event) {
        log.info("Sending user event to topic {}: {}", userEventsTopic, event);
        kafkaTemplate.send(userEventsTopic, event.getEmail(), event);
    }
}
