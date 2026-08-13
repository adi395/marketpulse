package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.dto.FundamentalsDto;
import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.service.ChartService;
import com.marketpulse.backend.service.CompanyService;
import com.marketpulse.backend.service.FundamentalsService;
import com.marketpulse.backend.service.QuoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final QuoteService quoteService;
    private final ChartService chartService;
    private final FundamentalsService fundamentalsService;

    public CompanyController(CompanyService companyService, QuoteService quoteService,
                              ChartService chartService, FundamentalsService fundamentalsService) {
        this.companyService = companyService;
        this.quoteService = quoteService;
        this.chartService = chartService;
        this.fundamentalsService = fundamentalsService;
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

    @GetMapping("/{ticker}/fundamentals")
    public FundamentalsDto getFundamentals(@PathVariable String ticker) {
        return fundamentalsService.getFundamentals(ticker);
    }
}
