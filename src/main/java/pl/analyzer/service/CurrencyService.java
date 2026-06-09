package pl.analyzer.service; // Dostosuj pakiet do swojej struktury

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CurrencyService {

    private final RestClient restClient = RestClient.create();
    private final Map<String, BigDecimal> rateCache = new ConcurrentHashMap<>();

    /**
     * Zwraca kurs waluty z NBP lub z lokalnego cache, jeśli NBP nie działa.
     * Zwraca Optional.empty(), jeśli całkowicie brakuje danych.
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
                BigDecimal rate = response.rates().get(0).mid();

                rateCache.put(upperCode, rate);
                log.info("Zapisano do cache kurs dla {}: {}", upperCode, rate);

                return Optional.of(rate);
            }
        } catch (Exception e) {
            log.error("⚠️ Błąd pobierania kursu z NBP dla {}: {}. Szukam w cache...", upperCode, e.getMessage());

            if (rateCache.containsKey(upperCode)) {
                BigDecimal cachedRate = rateCache.get(upperCode);
                log.info("ℹ️ Sukces planu awaryjnego! Zwracam kurs z cache dla {}: {}", upperCode, cachedRate);
                return Optional.of(cachedRate);
            }
        }

        log.error("❌ Krytyczny błąd: NBP leży, a lokalny cache dla {} jest pusty!", upperCode);
        return Optional.empty();
    }
}

// Pomocnicze rekordy DTO do sparsowania JSONa z NBP
record NbpResponse(List<NbpRate> rates) {}
record NbpRate(BigDecimal mid) {}