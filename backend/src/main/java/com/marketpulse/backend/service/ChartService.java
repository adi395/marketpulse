package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.model.ChartRange;
import com.marketpulse.backend.provider.ChartDataProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChartService {
    private final ChartDataProvider chartDataProvider;

    public ChartService(ChartDataProvider chartDataProvider) {
        this.chartDataProvider = chartDataProvider;
    }

    /**
     * Returns daily price bars for the requested time window. The provider
     * caches the full series, so slicing here is cheap and spends no extra
     * upstream calls when the user switches ranges.
     */
    public List<PriceBarDto> getHistoricalPrices(String ticker, ChartRange range) {
        List<PriceBarDto> full = chartDataProvider.getHistoricalPrices(ticker.toUpperCase());
        if (range == ChartRange.MAX) {
            return full;
        }
        LocalDate cutoff = LocalDate.now().minusDays(range.days());
        return full.stream()
                .filter(bar -> LocalDate.parse(bar.getDate()).isAfter(cutoff))
                .toList();
    }
}
