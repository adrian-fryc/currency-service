package pl.analyzer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KafkaConsumerService {

    // Ta adnotacja sprawia, że Spring cały czas "podsłuchuje" dany temat
    @KafkaListener(topics = "test-topic", groupId = "currency-analyzer-group")
    public void consume(String message) {
        try {
            String[] parts = message.split(";");
            String currencyCode = parts[0];
            BigDecimal rate = new BigDecimal(parts[1]);

            System.out.println("📥 [Spring Consumer] Przetwarzam kurs dla " + currencyCode + ": " + rate);

            // Definiujemy próg ostrzegawczy, np. 4.00 zł
            BigDecimal threshold = new BigDecimal("4.00");

            // compareTo zwraca:
            //  1 jeśli rate jest WIĘKSZY niż threshold
            //  0 jeśli są RÓWNE
            // -1 jeśli rate jest MNIEJSZY
            if (rate.compareTo(threshold) > 0) {
                System.out.println("🔥 [ALERT] Kurs " + currencyCode + " przekroczył barierę 4.00 PLN! Wynosi: " + rate);
            }else{
                System.out.println(rate);
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd podczas parsowania wiadomości z Kafki: " + e.getMessage() + " o treści: " + message);
        }
    }
}