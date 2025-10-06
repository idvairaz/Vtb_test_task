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

    @KafkaListener(topics = "${app.kafka.topic}", concurrency = "${app.kafka.concurrency:3}")
    @Transactional
    public void consume(String message) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Received empty or null message, skipping...");
            return;
        }
        try {
            // Логируем получение сообщения из Kafka
            log.info("[Read from Kafka] {}", message);

            // Парсим JSON сообщение
            JsonNode jsonNode = objectMapper.readTree(message);

            String msgUuid = jsonNode.get("msg_uuid").asText();
            Boolean head = jsonNode.get("head").asBoolean();
            long timestamp = System.currentTimeMillis() / 1000; // UNIX time

            // Проверяем дубликаты
            if (messageRepository.existsByMsgUuid(msgUuid)) {
                log.warn("Duplicate message detected: {}", msgUuid);
                return;
            }

            // Создаем и сохраняем запись в БД
            Message dbMessage = new Message();
            dbMessage.setMsgUuid(msgUuid);
            dbMessage.setHead(head);
            dbMessage.setTimeRq(timestamp);

            messageRepository.save(dbMessage);

            // Логируем запись в БД
            log.info("[Write to DB] {{ \"msgUuid\": \"{}\", \"head\": {}, \"timeRq\": \"{}\" }}",
                    msgUuid, head, timestamp);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}
