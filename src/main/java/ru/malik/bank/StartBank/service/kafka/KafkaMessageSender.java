package ru.malik.bank.StartBank.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class KafkaMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(KafkaMessageSender.class);

    public KafkaMessageSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(String topic, Object messageObject) {
        try {
            String message = objectMapper.writeValueAsString(messageObject);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message).toCompletableFuture();

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Ошибка при отправке сообщения в Kafka: {}", ex.getMessage());
                } else {
                    RecordMetadata metadata = result.getRecordMetadata();
                    logger.info("Сообщение отправлено в {} [partition {}, offset {}]",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });

        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации объекта: {}", e.getMessage());
        }
    }
}