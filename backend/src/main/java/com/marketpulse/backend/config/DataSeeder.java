package com.marketpulse.backend.config;

import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CompanyRepository companyRepository;

    public DataSeeder(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public void run(String... args) {
        if (companyRepository.count() > 0) return;

        List<Company> seed = List.of(
            Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("MSFT").name("Microsoft Corporation").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("NVDA").name("NVIDIA Corporation").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("AMZN").name("Amazon.com, Inc.").sector("Consumer Discretionary").exchange("NASDAQ").build(),
            Company.builder().ticker("GOOGL").name("Alphabet Inc.").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("META").name("Meta Platforms, Inc.").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("TSLA").name("Tesla, Inc.").sector("Consumer Discretionary").exchange("NASDAQ").build(),
            Company.builder().ticker("AMD").name("Advanced Micro Devices, Inc.").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("AVGO").name("Broadcom Inc.").sector("Technology").exchange("NASDAQ").build(),
            Company.builder().ticker("JPM").name("JPMorgan Chase & Co.").sector("Financials").exchange("NYSE").build(),
            Company.builder().ticker("GS").name("The Goldman Sachs Group, Inc.").sector("Financials").exchange("NYSE").build(),
            Company.builder().ticker("MS").name("Morgan Stanley").sector("Financials").exchange("NYSE").build(),
            Company.builder().ticker("BAC").name("Bank of America Corporation").sector("Financials").exchange("NYSE").build()
        );

        companyRepository.saveAll(seed);
    }
}
