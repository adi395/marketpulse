package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.model.ChartRange;
import com.marketpulse.backend.provider.ChartDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartServiceTest {

    @Mock
    private ChartDataProvider chartDataProvider;

    @InjectMocks
    private ChartService chartService;

    private static PriceBarDto barDaysAgo(int days) {
        String date = LocalDate.now().minusDays(days).toString();
        return PriceBarDto.builder().date(date).open(1.0).high(1.0).low(1.0).close(1.0).volume(1L).build();
    }

    private void provideFullSeries() {
        when(chartDataProvider.getHistoricalPrices("AAPL")).thenReturn(List.of(
                barDaysAgo(400),
                barDaysAgo(300),
                barDaysAgo(100),
                barDaysAgo(40),
                barDaysAgo(5)
        ));
    }

    @Test
    void oneMonthKeepsOnlyRecentBars() {
        provideFullSeries();
        List<PriceBarDto> bars = chartService.getHistoricalPrices("AAPL", ChartRange.M1).bars();
        assertThat(bars).hasSize(1); // only the 5-day-old bar is within ~30 days
    }

    @Test
    void threeMonthsKeepsBarsWithinNinetyDays() {
        provideFullSeries();
        List<PriceBarDto> bars = chartService.getHistoricalPrices("AAPL", ChartRange.M3).bars();
        assertThat(bars).hasSize(2); // 5 and 40 days old
    }

    @Test
    void oneYearKeepsBarsWithinaYear() {
        provideFullSeries();
        List<PriceBarDto> bars = chartService.getHistoricalPrices("AAPL", ChartRange.Y1).bars();
        assertThat(bars).hasSize(4); // all but the 400-day-old bar
    }

    @Test
    void maxReturnsTheFullSeriesUnfiltered() {
        provideFullSeries();
        List<PriceBarDto> bars = chartService.getHistoricalPrices("AAPL", ChartRange.MAX).bars();
        assertThat(bars).hasSize(5);
    }

    @Test
    void rangeParsingRejectsUnsupportedValues() {
        assertThat(ChartRange.fromParam("1M")).isEqualTo(ChartRange.M1);
        assertThat(ChartRange.fromParam(null)).isEqualTo(ChartRange.M6);
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, () -> ChartRange.fromParam("10Y"));
    }
}
