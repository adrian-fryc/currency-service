package pl.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.analyzer.model.CurrencyRate;

import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // Spring sam domyśli się, jak napisać zapytanie SQL na podstawie nazwy metody!
    // Wygeneruje: SELECT * FROM currency_rates WHERE currency_code = ?
    Optional<CurrencyRate> findByCurrencyCode(String currencyCode);
}
