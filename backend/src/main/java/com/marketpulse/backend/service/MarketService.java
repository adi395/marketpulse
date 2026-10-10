package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.dto.MarketOverviewDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.repository.CompanyRepository;
import com.marketpulse.backend.resilience.ResilientLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private static final String SOURCE = "Finnhub";

    private final CompanyRepository companyRepository;
    private final QuoteService quoteService;

    public MarketService(CompanyRepository companyRepository, QuoteService quoteService) {
        this.companyRepository = companyRepository;
        this.quoteService = quoteService;
    }

    public MarketOverviewDto getOverview() {
        List<MarketMoverDto> movers = new ArrayList<>();
        Long asOf = null;
        boolean stale = false;

        for (Company company : companyRepository.findAll()) {
            try {
                ResilientLoader.Result<QuoteDto> result = quoteService.getQuote(company.getTicker());
                QuoteDto quote = result.value();
                stale = stale || result.stale();
                movers.add(toMover(company, quote));
                if (quote.getTimestamp() != null) {
                    asOf = asOf == null ? quote.getTimestamp() : Math.max(asOf, quote.getTimestamp());
                }
            } catch (Exception e) {
                log.warn("Skipping {} in market overview: {}", company.getTicker(), e.getMessage());
            }
        }

        movers.sort(Comparator.comparing(
                MarketMoverDto::getPercentChange,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return MarketOverviewDto.builder()
                .source(SOURCE)
                .asOf(asOf)
                .stale(stale)
                .companies(movers)
                .build();
    }

    private MarketMoverDto toMover(Company company, QuoteDto quote) {
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
    }
}
