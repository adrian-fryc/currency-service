package pl.analyzer.controller; // Dostosuj pakiet

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.analyzer.service.CurrencyService; // Dostosuj importy

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    // Wstrzykujemy serwis przez Lombok (@RequiredArgsConstructor)
    private final CurrencyService currencyService;

    @GetMapping("/{code}")
    public ResponseEntity<BigDecimal> getRate(@PathVariable String code) {
        log.info("Mikroserwis walutowy otrzymał żądanie HTTP dla: {}", code);

        return currencyService.getRate(code)
                .map(ResponseEntity::ok) // Jeśli Optional ma wartość -> 200 OK z kursem
                .orElseGet(() -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()); // Jeśli pusty -> 503
    }
}