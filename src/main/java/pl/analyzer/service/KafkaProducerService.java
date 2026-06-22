package pl.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.analyzer.model.CurrencyEvent;

@Service
@Slf4j
@RequiredArgsConstructor // Lombok wygeneruje konstruktor dla pola final
public class KafkaProducerService {

    // Zmieniamy drugi typ generyczny na nasz rekord CurrencyEvent
    private final KafkaTemplate<String, CurrencyEvent> kafkaTemplate;

    public void sendMessage(CurrencyEvent event) {
        log.info("👉 [Spring Producer] Wysyłam obiekt JSON do Kafki: {}", event);

        // Jako klucz wiadomości (drugi parametr) przekazujemy kod waluty (np. "USD").
        // To kluczowa praktyka w Kafce – gwarantuje, że zdarzenia dla tej samej waluty
        // zawsze trafią na tę samą partycję (przyda nam się to przy Opcji B!).
        kafkaTemplate.send("test-topic", event.currencyCode(), event);
    }
}