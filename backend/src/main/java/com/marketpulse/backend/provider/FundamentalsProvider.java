package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.FundamentalsDto;

public interface FundamentalsProvider {
    FundamentalsDto getFundamentals(String ticker);
}
