package com.vtb.kafkaconsumer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtb.kafkaconsumer.entity.Message;
import com.vtb.kafkaconsumer.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService{
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DelayService delayService;

    @KafkaListener(topics = "${app.kafka.topic}", concurrency = "${app.kafka.concurrency:3}")
    @Transactional
    public void consume(String message) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Received empty or null message, skipping...");
            return;
        }
        try {
            log.info("[Read from Kafka] {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);

            String msgUuid = jsonNode.get("msg_uuid").asText();
            Boolean head = jsonNode.get("head").asBoolean();
            long timestamp = System.currentTimeMillis() / 1000;
            if (messageRepository.existsByMsgUuid(msgUuid)) {
                log.warn("Duplicate message detected: {}", msgUuid);
                return;
            }

            long currentDelay = delayService.getCurrentDelay();
            log.info("Applying processing delay: {} ms", currentDelay);
            try {
                Thread.sleep(currentDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Message processing was interrupted");
                return;
            }
            Message dbMessage = new Message();
            dbMessage.setMsgUuid(msgUuid);
            dbMessage.setHead(head);
            dbMessage.setTimeRq(timestamp);

            messageRepository.save(dbMessage);

            log.info("[Write to DB] {{ \"msgUuid\": \"{}\", \"head\": {}, \"timeRq\": \"{}\" }}",
                    msgUuid, head, timestamp);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}
