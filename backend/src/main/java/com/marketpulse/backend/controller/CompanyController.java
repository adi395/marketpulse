package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.service.CompanyService;
import com.marketpulse.backend.service.QuoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final QuoteService quoteService;

    public CompanyController(CompanyService companyService, QuoteService quoteService) {
        this.companyService = companyService;
        this.quoteService = quoteService;
    }

    @GetMapping("/{ticker}")
    public CompanyDto getCompany(@PathVariable String ticker) {
        return companyService.getByTicker(ticker);
    }

    @GetMapping("/{ticker}/quote")
    public QuoteDto getQuote(@PathVariable String ticker) {
        return quoteService.getQuote(ticker);
    }
}
