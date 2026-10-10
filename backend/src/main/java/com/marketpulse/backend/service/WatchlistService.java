package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.model.Company;
import com.marketpulse.backend.model.WatchlistItem;
import com.marketpulse.backend.repository.CompanyRepository;
import com.marketpulse.backend.repository.WatchlistRepository;
import com.marketpulse.backend.util.Tickers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Per-user watchlist operations. Every method is scoped by {@code userId}, so a
 * caller can only ever see or modify their own rows — the ownership boundary is
 * enforced in the query, not just in the UI.
 */
@Service
public class WatchlistService {
    private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistRepository watchlistRepository;
    private final CompanyRepository companyRepository;
    private final QuoteService quoteService;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            CompanyRepository companyRepository,
                            QuoteService quoteService) {
        this.watchlistRepository = watchlistRepository;
        this.companyRepository = companyRepository;
        this.quoteService = quoteService;
    }

    /** The user's saved tickers, enriched with quotes. A failing quote yields a
     *  row with null price fields rather than dropping the saved item. */
    @Transactional(readOnly = true)
    public List<MarketMoverDto> getWatchlist(String userId) {
        return watchlistRepository.findByUserIdOrderByTickerAsc(userId).stream()
                .map(item -> toMover(item.getTicker()))
                .toList();
    }

    /** Adds a ticker; returns false if it was already on the list. */
    @Transactional
    public boolean add(String userId, String ticker) {
        String normalized = Tickers.require(ticker);
        if (watchlistRepository.existsByUserIdAndTicker(userId, normalized)) {
            return false;
        }
        watchlistRepository.save(
                WatchlistItem.builder().userId(userId).ticker(normalized).build());
        return true;
    }

    /** Removes the caller's own ticker; returns false if it wasn't on their list. */
    @Transactional
    public boolean remove(String userId, String ticker) {
        return watchlistRepository.deleteByUserIdAndTicker(userId, Tickers.require(ticker)) > 0;
    }

    private MarketMoverDto toMover(String ticker) {
        Company company = companyRepository.findByTickerIgnoreCase(ticker).orElse(null);
        MarketMoverDto.MarketMoverDtoBuilder builder = MarketMoverDto.builder()
                .ticker(ticker)
                .name(company != null ? company.getName() : ticker)
                .sector(company != null ? company.getSector() : null)
                .exchange(company != null ? company.getExchange() : null);
        try {
            QuoteDto quote = quoteService.getQuote(ticker).value();
            builder.currentPrice(quote.getCurrentPrice())
                    .change(quote.getChange())
                    .percentChange(quote.getPercentChange())
                    .dayHigh(quote.getDayHigh())
                    .dayLow(quote.getDayLow())
                    .openPrice(quote.getOpenPrice());
        } catch (Exception e) {
            log.warn("No quote for watchlisted {}: {}", ticker, e.getMessage());
        }
        return builder.build();
    }
}
