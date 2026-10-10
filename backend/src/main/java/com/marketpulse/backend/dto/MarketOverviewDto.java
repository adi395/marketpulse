package com.marketpulse.backend.dto;

import lombok.*;

import java.util.List;

/**
 * Wraps the market overview with provenance metadata so the UI can show where
 * the data came from and how fresh it is.
 *
 * @param source the upstream data provider (e.g. "Finnhub")
 * @param asOf   latest quote timestamp across all companies (unix seconds), or
 *               null if no company returned a timestamp
 * @param stale  true if any company's quote was served from the last-good cache
 *               after a provider failure
 * @param companies the per-company movers, sorted by percent change
 */
@Builder
public record MarketOverviewDto(
        String source,
        Long asOf,
        boolean stale,
        List<MarketMoverDto> companies
) {}
