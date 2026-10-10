package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.provider.MarketDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private MarketDataProvider provider;

    @InjectMocks
    private QuoteService quoteService;

    @Test
    void returnsFreshQuoteNotStale() {
        when(provider.getQuote("AAPL")).thenReturn(
                QuoteDto.builder().ticker("AAPL").currentPrice(100.0).percentChange(1.0).build());

        var result = quoteService.getQuote("aapl"); // also exercises normalization

        assertThat(result.value().getCurrentPrice()).isEqualTo(100.0);
        assertThat(result.stale()).isFalse();
    }

    @Test
    void propagatesProviderFailureWhenNoPriorSuccess() {
        when(provider.getQuote(anyString())).thenThrow(new RuntimeException("Finnhub down"));

        assertThatThrownBy(() -> quoteService.getQuote("AAPL"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Finnhub down");
    }
}
