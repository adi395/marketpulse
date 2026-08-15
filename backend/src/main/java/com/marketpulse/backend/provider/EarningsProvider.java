package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.EarningsDto;
import java.util.List;

public interface EarningsProvider {
    List<EarningsDto> getEarnings(String ticker);
}
