package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.exception.CompanyNotFoundException;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void returnsCompanyDtoWhenFound() {
        Company apple = Company.builder()
                .ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build();
        when(companyRepository.findByTickerIgnoreCase("AAPL")).thenReturn(Optional.of(apple));

        CompanyDto dto = companyService.getByTicker("AAPL");

        assertThat(dto.getTicker()).isEqualTo("AAPL");
        assertThat(dto.getName()).isEqualTo("Apple Inc.");
        assertThat(dto.getExchange()).isEqualTo("NASDAQ");
    }

    @Test
    void throwsWhenCompanyMissing() {
        when(companyRepository.findByTickerIgnoreCase("ZZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getByTicker("ZZZZ"))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("ZZZZ");
    }
}
