package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.provider.ChartDataProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {
    private final ChartDataProvider chartDataProvider;

    public ChartService(ChartDataProvider chartDataProvider) {
        this.chartDataProvider = chartDataProvider;
    }

    public List<PriceBarDto> getHistoricalPrices(String ticker) {
        return chartDataProvider.getHistoricalPrices(ticker.toUpperCase());
    }
}
