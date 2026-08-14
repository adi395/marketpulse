package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.SecFilingDto;
import java.util.List;

public interface FilingsProvider {
    List<SecFilingDto> getFilings(String ticker);
}
