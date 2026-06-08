package pl.analyzer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final RestClient restClient = RestClient.create();

    @GetMapping("/{code}")
    public ResponseEntity<BigDecimal> getRate(@PathVariable String code) {
        log.info("Mikroserwis walutowy otrzymał żądanie dla: {}", code);

        try {
            // Strzał do prawdziwego API NBP (Format Single A)
            String url = "http://api.nbp.pl/api/exchangerates/rates/a/" + code + "/?format=json";

            NbpResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NbpResponse.class);

            if (response != null && !response.rates().isEmpty()) {
                BigDecimal rate = response.rates().get(0).mid();
                return ResponseEntity.ok(rate);
            }
        } catch (Exception e) {
            log.error("Błąd pobierania kursu z NBP: {}", e.getMessage());
        }

        // Awaryjny fallback (np. gdy NBP leży)
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(BigDecimal.ONE);
    }
}

// Pomocnicze rekordy DTO do sparsowania JSONa z NBP
record NbpResponse(List<NbpRate> rates) {}
record NbpRate(BigDecimal mid) {}