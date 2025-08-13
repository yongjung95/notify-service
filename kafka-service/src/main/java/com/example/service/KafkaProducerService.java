package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendTopicMessage(String topic, String key, Object data) {
        kafkaTemplate.send(topic, key, data);
        log.info("Sent message to topic {} : key = {}, data = {}", topic, key, data);
    }
}
