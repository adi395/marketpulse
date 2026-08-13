package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.service.ChartService;
import com.marketpulse.backend.service.CompanyService;
import com.marketpulse.backend.service.QuoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final QuoteService quoteService;
    private final ChartService chartService;

    public CompanyController(CompanyService companyService, QuoteService quoteService, ChartService chartService) {
        this.companyService = companyService;
        this.quoteService = quoteService;
        this.chartService = chartService;
    }

    @GetMapping("/{ticker}")
    public CompanyDto getCompany(@PathVariable String ticker) {
        return companyService.getByTicker(ticker);
    }

    @GetMapping("/{ticker}/quote")
    public QuoteDto getQuote(@PathVariable String ticker) {
        return quoteService.getQuote(ticker);
    }

    @GetMapping("/{ticker}/chart")
    public List<PriceBarDto> getChart(@PathVariable String ticker) {
        return chartService.getHistoricalPrices(ticker);
    }
}
