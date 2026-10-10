package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.dto.EarningsDto;
import com.marketpulse.backend.dto.FundamentalsDto;
import com.marketpulse.backend.dto.NewsArticleDto;
import com.marketpulse.backend.dto.PriceBarDto;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.dto.SecFilingDto;
import com.marketpulse.backend.model.ChartRange;
import com.marketpulse.backend.resilience.ResilientLoader;
import com.marketpulse.backend.util.Tickers;
import com.marketpulse.backend.service.ChartService;
import com.marketpulse.backend.service.CompanyService;
import com.marketpulse.backend.service.EarningsService;
import com.marketpulse.backend.service.FilingsService;
import com.marketpulse.backend.service.FundamentalsService;
import com.marketpulse.backend.service.NewsService;
import com.marketpulse.backend.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final QuoteService quoteService;
    private final ChartService chartService;
    private final FundamentalsService fundamentalsService;
    private final NewsService newsService;
    private final FilingsService filingsService;
    private final EarningsService earningsService;

    public CompanyController(CompanyService companyService, QuoteService quoteService,
                              ChartService chartService, FundamentalsService fundamentalsService,
                              NewsService newsService, FilingsService filingsService,
                              EarningsService earningsService) {
        this.companyService = companyService;
        this.quoteService = quoteService;
        this.chartService = chartService;
        this.fundamentalsService = fundamentalsService;
        this.newsService = newsService;
        this.filingsService = filingsService;
        this.earningsService = earningsService;
    }

    @GetMapping("/{ticker}")
    public CompanyDto getCompany(@PathVariable String ticker) {
        return companyService.getByTicker(Tickers.require(ticker));
    }

    @GetMapping("/{ticker}/quote")
    public ResponseEntity<QuoteDto> getQuote(@PathVariable String ticker) {
        ResilientLoader.Result<QuoteDto> result = quoteService.getQuote(Tickers.require(ticker));
        return ResponseEntity.ok()
                .header("X-Data-Stale", String.valueOf(result.stale()))
                .body(result.value());
    }

    @GetMapping("/{ticker}/chart")
    public ResponseEntity<List<PriceBarDto>> getChart(@PathVariable String ticker,
                                                      @RequestParam(defaultValue = "6M") String range) {
        ChartService.ChartResult result =
                chartService.getHistoricalPrices(Tickers.require(ticker), ChartRange.fromParam(range));
        return ResponseEntity.ok()
                .header("X-Data-Stale", String.valueOf(result.stale()))
                .body(result.bars());
    }

    @GetMapping("/{ticker}/fundamentals")
    public FundamentalsDto getFundamentals(@PathVariable String ticker) {
        return fundamentalsService.getFundamentals(Tickers.require(ticker));
    }

    @GetMapping("/{ticker}/news")
    public List<NewsArticleDto> getNews(@PathVariable String ticker) {
        return newsService.getCompanyNews(Tickers.require(ticker));
    }

    @GetMapping("/{ticker}/filings")
    public List<SecFilingDto> getFilings(@PathVariable String ticker) {
        return filingsService.getFilings(Tickers.require(ticker));
    }

    @GetMapping("/{ticker}/earnings")
    public List<EarningsDto> getEarnings(@PathVariable String ticker) {
        return earningsService.getEarnings(Tickers.require(ticker));
    }
}
