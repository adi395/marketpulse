package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.service.MarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketController.class)
class MarketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MarketService marketService;

    @Test
    void returnsMarketOverviewAsJson() throws Exception {
        when(marketService.getOverview()).thenReturn(List.of(
                MarketMoverDto.builder()
                        .ticker("NVDA").name("NVIDIA Corporation").sector("Technology").exchange("NASDAQ")
                        .currentPrice(229.28).change(1.2).percentChange(0.53).build(),
                MarketMoverDto.builder()
                        .ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ")
                        .currentPrice(336.64).change(-3.78).percentChange(-1.11).build()
        ));

        mockMvc.perform(get("/api/market/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ticker").value("NVDA"))
                .andExpect(jsonPath("$[0].currentPrice").value(229.28))
                .andExpect(jsonPath("$[1].ticker").value("AAPL"))
                .andExpect(jsonPath("$[1].percentChange").value(-1.11));
    }
}
