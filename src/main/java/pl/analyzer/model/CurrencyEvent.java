package pl.analyzer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyEvent(
        String currencyCode,
        BigDecimal rate,
        LocalDateTime timestamp,
        String source // np. "NBP" lub "DATABASE"
) {}