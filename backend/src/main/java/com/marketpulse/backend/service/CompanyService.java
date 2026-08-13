package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import com.marketpulse.backend.exception.CompanyNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyDto getByTicker(String ticker) {
        Company company = companyRepository.findByTickerIgnoreCase(ticker)
                .orElseThrow(() -> new CompanyNotFoundException(ticker));
        return CompanyDto.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .sector(company.getSector())
                .exchange(company.getExchange())
                .build();
    }
}
