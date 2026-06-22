package pl.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.analyzer.model.CurrencyEvent;
import pl.analyzer.model.CurrencyRate;
import pl.analyzer.repository.CurrencyRateRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CurrencyService {

    private final RestClient restClient = RestClient.create();

    @Autowired
    private CurrencyRateRepository repository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Zwraca kurs waluty z NBP i zapisuje go do PostgreSQL.
     * W razie awarii NBP, pobiera ostatni znany kurs z bazy danych.
     */
    public Optional<BigDecimal> getRate(String currencyCode) {
        String upperCode = currencyCode.toUpperCase();

        try {
            String url = "http://api.nbp.pl/api/exchangerates/rates/a/" + upperCode + "/?format=json";

            NbpResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NbpResponse.class);

            if (response != null && !response.rates().isEmpty()) {
                BigDecimal rateVal = response.rates().get(0).mid();

                // --- NOWOŚĆ: ZAPIS DO BAZY DATY ZAMIAST CACHE ---
                // Sprawdzamy, czy mamy już tę walutę w bazie, żeby nadpisać rekord zamiast tworzyć duplikaty
                CurrencyRate currencyRate = repository.findByCurrencyCode(upperCode)
                        .orElse(new CurrencyRate());

                currencyRate.setCurrencyCode(upperCode);
                currencyRate.setRate(rateVal);
                currencyRate.setUpdatedAt(LocalDateTime.now());

                repository.save(currencyRate); // Tu Hibernate robi automatyczny INSERT lub UPDATE
                log.info("Zapisano do bazy PostgreSQL kurs dla {}: {}", upperCode, rateVal);
                String message = upperCode + ";" + rateVal; // Wyśle np. "USD;4.02"
                kafkaProducerService.sendMessage(new CurrencyEvent(upperCode, rateVal, LocalDateTime.now(), "NBP"));

                return Optional.of(rateVal);
            }
        } catch (Exception e) {
            log.error("⚠️ Błąd pobierania kursu z NBP dla {}: {}. Szukam w bazie danych...", upperCode, e.getMessage());

            // --- NOWOŚĆ: PLAN AWARYJNY Z BAZY ---
            Optional<CurrencyRate> databaseRate = repository.findByCurrencyCode(upperCode);
            if (databaseRate.isPresent()) {
                BigDecimal savedRate = databaseRate.get().getRate();
                log.info("ℹ️ Sukces planu awaryjnego! Zwracam kurs z bazy danych dla {}: {}", upperCode, savedRate);

                String message = upperCode + ";" + savedRate; // Wyśle np. "USD;4.02"
                kafkaProducerService.sendMessage(new CurrencyEvent(upperCode, savedRate, LocalDateTime.now(), "DATABASE"));

                return Optional.of(savedRate);
            }
        }

        log.error("❌ Krytyczny błąd: NBP leży, a baza danych dla {} jest pusta!", upperCode);
        return Optional.empty();
    }
}

// Pomocnicze rekordy DTO do sparsowania JSONa z NBP
record NbpResponse(List<NbpRate> rates) {}
record NbpRate(BigDecimal mid) {}