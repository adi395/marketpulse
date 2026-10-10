package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private QuoteService quoteService;

    @InjectMocks
    private MarketService marketService;

    private static Company company(String ticker) {
        return Company.builder().ticker(ticker).name(ticker + " Inc.").sector("Technology").exchange("NASDAQ").build();
    }

    private static QuoteDto quote(double percentChange) {
        return QuoteDto.builder().currentPrice(100.0).change(1.0).percentChange(percentChange).build();
    }

    @Test
    void overviewIsSortedByPercentChangeDescending() {
        when(companyRepository.findAll()).thenReturn(List.of(company("AAA"), company("BBB"), company("CCC")));
        when(quoteService.getQuote("AAA")).thenReturn(quote(1.5));
        when(quoteService.getQuote("BBB")).thenReturn(quote(5.0));
        when(quoteService.getQuote("CCC")).thenReturn(quote(-2.0));

        List<MarketMoverDto> overview = marketService.getOverview();

        assertThat(overview).extracting(MarketMoverDto::getTicker).containsExactly("BBB", "AAA", "CCC");
    }

    @Test
    void failingQuoteIsSkippedRatherThanFailingTheWholeOverview() {
        when(companyRepository.findAll()).thenReturn(List.of(company("AAA"), company("BBB")));
        when(quoteService.getQuote("AAA")).thenReturn(quote(1.0));
        when(quoteService.getQuote("BBB")).thenThrow(new RuntimeException("provider down"));

        List<MarketMoverDto> overview = marketService.getOverview();

        assertThat(overview).extracting(MarketMoverDto::getTicker).containsExactly("AAA");
    }

    @Test
    void mapsQuoteFieldsOntoMover() {
        when(companyRepository.findAll()).thenReturn(List.of(company("AAA")));
        when(quoteService.getQuote("AAA")).thenReturn(quote(3.25));

        MarketMoverDto mover = marketService.getOverview().getFirst();

        assertThat(mover.getName()).isEqualTo("AAA Inc.");
        assertThat(mover.getSector()).isEqualTo("Technology");
        assertThat(mover.getCurrentPrice()).isEqualTo(100.0);
        assertThat(mover.getPercentChange()).isEqualTo(3.25);
    }
}
