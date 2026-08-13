package com.marketpulse.backend.exception;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String ticker) {
        super("Company not found: " + ticker);
    }
}
