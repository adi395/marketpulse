package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.PriceBarDto;
import java.util.List;

public interface ChartDataProvider {
    List<PriceBarDto> getHistoricalPrices(String ticker);
}
