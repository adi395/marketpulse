package com.marketpulse.backend;

import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.provider.MarketDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test of the primary user journey through the full Spring stack
 * (controllers -> services -> JPA on in-memory H2). The external market-data
 * provider is mocked so the test is hermetic and needs no network or API keys —
 * which is what makes it safe to run in CI.
 *
 * Journey: see the market overview -> open a company -> read its quote ->
 * add it to a watchlist -> see it on the watchlist -> remove it.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PrimaryJourneyTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MarketDataProvider marketDataProvider;

    private static final String USER = "journey-user";

    @Test
    void userCanBrowseAndManageAWatchlist() throws Exception {
        when(marketDataProvider.getQuote(anyString())).thenReturn(
                QuoteDto.builder().ticker("AAPL").currentPrice(100.0)
                        .change(1.0).percentChange(1.0).timestamp(1_000L).build());

        // 1. Market overview lists the seeded companies with provenance.
        mvc.perform(get("/api/market/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("Finnhub"))
                .andExpect(jsonPath("$.companies").isNotEmpty());

        // 2. Open a company detail.
        mvc.perform(get("/api/companies/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"));

        // 3. Read its quote.
        mvc.perform(get("/api/companies/AAPL/quote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPrice").value(100.0))
                .andExpect(header().string("X-Data-Stale", "false"));

        // 4. Add it to the watchlist.
        mvc.perform(post("/api/watchlist/AAPL").header("X-User-Id", USER))
                .andExpect(status().isCreated());

        // 5. It appears on the watchlist, enriched with a quote.
        mvc.perform(get("/api/watchlist").header("X-User-Id", USER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticker").value("AAPL"))
                .andExpect(jsonPath("$[0].currentPrice").value(100.0));

        // 6. Remove it; the watchlist is empty again.
        mvc.perform(delete("/api/watchlist/AAPL").header("X-User-Id", USER))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/watchlist").header("X-User-Id", USER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
