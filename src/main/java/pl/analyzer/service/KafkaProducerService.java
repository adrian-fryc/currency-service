package pl.analyzer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Spring automatycznie wstrzyknie tutaj skonfigurowany KafkaTemplate
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        String topicName = "test-topic";

        // Wysyłamy wiadomość do Kafki do konkretnego tematu
        kafkaTemplate.send(topicName, message);

        System.out.println("👉 [Spring Producer] Wysłano wiadomość: " + message);
    }
}