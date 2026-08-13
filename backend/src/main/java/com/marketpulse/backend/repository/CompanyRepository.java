package com.marketpulse.backend.repository;

import com.marketpulse.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTickerIgnoreCase(String ticker);
}
