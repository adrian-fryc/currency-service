package pl.analyzer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic currencyTopic() {
        return TopicBuilder.name("test-topic")
                .partitions(3) // Wymuszamy 3 partycje!
                .replicas(1)   // Mamy 1 brokera w Dockerze, więc 1 replika wystarczy
                .build();
    }
}
