package pl.analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.analyzer.service.KafkaProducerService;

@SpringBootApplication
public class CurrencyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner testKafka(KafkaProducerService producerService) {
        return args -> {
            System.out.println("🚀 Odpalam testowe wysyłanie do Kafki...");
            producerService.sendMessage("Siemanko! To ja, Twój Spring Boot rozmawiający przez Kafkę! 🌪️");
        };
    }
}