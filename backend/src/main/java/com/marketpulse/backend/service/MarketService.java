package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Assembles the market overview shown on the dashboard: every tracked company
 * paired with its latest quote, sorted by percent change (biggest gainers
 * first). Quotes come from the cached {@link QuoteService}, and a single
 * failing ticker is skipped rather than failing the whole overview.
 */
@Service
public class MarketService {
    private static final Logger log = LoggerFactory.getLogger(MarketService.class);

    private final CompanyRepository companyRepository;
    private final QuoteService quoteService;

    public MarketService(CompanyRepository companyRepository, QuoteService quoteService) {
        this.companyRepository = companyRepository;
        this.quoteService = quoteService;
    }

    public List<MarketMoverDto> getOverview() {
        return companyRepository.findAll().stream()
                .map(this::toMover)
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(
                        MarketMoverDto::getPercentChange,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private MarketMoverDto toMover(Company company) {
        try {
            QuoteDto quote = quoteService.getQuote(company.getTicker());
            return MarketMoverDto.builder()
                    .ticker(company.getTicker())
                    .name(company.getName())
                    .sector(company.getSector())
                    .exchange(company.getExchange())
                    .currentPrice(quote.getCurrentPrice())
                    .change(quote.getChange())
                    .percentChange(quote.getPercentChange())
                    .dayHigh(quote.getDayHigh())
                    .dayLow(quote.getDayLow())
                    .openPrice(quote.getOpenPrice())
                    .build();
        } catch (Exception e) {
            log.warn("Skipping {} in market overview: {}", company.getTicker(), e.getMessage());
            return null;
        }
    }
}
