package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.model.ChartRange;
import com.marketpulse.backend.provider.ChartDataProvider;
import com.marketpulse.backend.resilience.ResilientLoader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class ChartService {
    private final ChartDataProvider chartDataProvider;

    // 6h freshness; NO retry — Alpha Vantage is quota-bound (25 calls/day), so a
    // retry would just burn the daily budget. Last-good fallback on failure.
    private final ResilientLoader<List<PriceBarDto>> loader =
            new ResilientLoader<>("charts", Duration.ofHours(6), 500, 1);

    public ChartService(ChartDataProvider chartDataProvider) {
        this.chartDataProvider = chartDataProvider;
    }

    /** Sliced price bars plus whether the underlying series is being served stale. */
    public record ChartResult(List<PriceBarDto> bars, boolean stale) {}

    /**
     * Returns daily price bars for the requested time window. The full series is
     * fetched once and cached, so slicing here is cheap and spends no extra
     * upstream calls when the user switches ranges.
     */
    public ChartResult getHistoricalPrices(String ticker, ChartRange range) {
        String key = ticker.toUpperCase();
        ResilientLoader.Result<List<PriceBarDto>> result =
                loader.get(key, () -> chartDataProvider.getHistoricalPrices(key));

        List<PriceBarDto> full = result.value();
        if (range == ChartRange.MAX) {
            return new ChartResult(full, result.stale());
        }
        LocalDate cutoff = LocalDate.now().minusDays(range.days());
        List<PriceBarDto> sliced = full.stream()
                .filter(bar -> LocalDate.parse(bar.getDate()).isAfter(cutoff))
                .toList();
        return new ChartResult(sliced, result.stale());
    }
}
