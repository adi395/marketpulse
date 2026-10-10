package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.MarketOverviewDto;
import com.marketpulse.backend.service.MarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/overview")
    public MarketOverviewDto getOverview() {
        return marketService.getOverview();
    }
}
