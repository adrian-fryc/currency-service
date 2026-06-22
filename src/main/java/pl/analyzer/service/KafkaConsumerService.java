package pl.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.analyzer.model.CurrencyEvent;

import java.math.BigDecimal;

@Service
@Slf4j
public class KafkaConsumerService {

    // KONSUMENT NR 1 - OLA
    @KafkaListener(topics = "test-topic", groupId = "currency-analyzer-group")
    public void consumeOla(CurrencyEvent event) {
        log.info("👩‍💻 [Konsument OLA] Odebrałam JSON: {} dla waluty: {}", event.rate(), event.currencyCode());
    }

    // KONSUMENT NR 2 - BORYS
    @KafkaListener(topics = "test-topic", groupId = "currency-analyzer-group")
    public void consumeBorys(CurrencyEvent event) {
        log.info("👨‍💻 [Konsument BORYS] Odebrałem JSON: {} dla waluty: {}", event.rate(), event.currencyCode());
    }

    /*
    // Ta adnotacja sprawia, że Spring cały czas "podsłuchuje" dany temat
    @KafkaListener(topics = "test-topic", groupId = "currency-analyzer-group")
    public void consume(CurrencyEvent event) {
        try {
            log.info("📥 [Spring Consumer] Odebrano i automatycznie sparsowano JSON: {}", event);

            String currencyCode = event.currencyCode();
            BigDecimal rate = event.rate();

            // Definiujemy próg ostrzegawczy, np. 4.00 zł
            BigDecimal threshold = new BigDecimal("4.00");

            // compareTo zwraca:
            //  1 jeśli rate jest WIĘKSZY niż threshold
            //  0 jeśli są RÓWNE
            // -1 jeśli rate jest MNIEJSZY
            if (rate.compareTo(threshold) > 0) {
                log.warn("🔥 [ALERT] Kurs {} przekroczył barierę 4.00 PLN! Wynosi: {} (Źródło danych: {})",
                        currencyCode, rate, event.source());
            }else{
                System.out.println(rate);
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd podczas parsowania wiadomości z Kafki: " + e.getMessage() + " o treści: " + event.toString());
        }
    }

     */
}