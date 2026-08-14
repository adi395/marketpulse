package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.SecFilingDto;
import com.marketpulse.backend.provider.FilingsProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilingsService {
    private final FilingsProvider filingsProvider;

    public FilingsService(FilingsProvider filingsProvider) {
        this.filingsProvider = filingsProvider;
    }

    public List<SecFilingDto> getFilings(String ticker) {
        return filingsProvider.getFilings(ticker.toUpperCase());
    }
}
