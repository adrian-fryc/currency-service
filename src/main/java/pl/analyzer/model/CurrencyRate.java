package pl.analyzer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rates") // tak będzie się nazywać tabela w Postgresie
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatyczny auto-increment dla ID
    private Long id;

    @Column(name = "currency_code", unique = true, nullable = false, length = 3)
    private String currencyCode; // np. "EUR", "USD"

    @Column(name = "rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal rate; // kurs waluty

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Konstruktory ---
    public CurrencyRate() {} // Konstruktor bezargumentowy jest WYMAGANY przez JPA

    public CurrencyRate(String currencyCode, BigDecimal rate, LocalDateTime updatedAt) {
        this.currencyCode = currencyCode;
        this.rate = rate;
        this.updatedAt = updatedAt;
    }

    // --- Gettery i Settery (lub adnotacje z Lombok, jeśli używasz) ---
    public Long getId() { return id; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}