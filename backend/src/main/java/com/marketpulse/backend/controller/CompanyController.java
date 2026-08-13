package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.CompanyDto;
import com.marketpulse.backend.service.CompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{ticker}")
    public CompanyDto getCompany(@PathVariable String ticker) {
        return companyService.getByTicker(ticker);
    }
}
