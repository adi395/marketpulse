package com.marketpulse.backend.controller;

import com.marketpulse.backend.dto.MarketMoverDto;
import com.marketpulse.backend.service.WatchlistService;
import com.marketpulse.backend.util.UserIds;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<MarketMoverDto> list(@RequestHeader("X-User-Id") String userId) {
        return watchlistService.getWatchlist(UserIds.require(userId));
    }

    @PostMapping("/{ticker}")
    public ResponseEntity<Void> add(@RequestHeader("X-User-Id") String userId,
                                    @PathVariable String ticker) {
        watchlistService.add(UserIds.require(userId), ticker);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<Void> remove(@RequestHeader("X-User-Id") String userId,
                                       @PathVariable String ticker) {
        watchlistService.remove(UserIds.require(userId), ticker);
        return ResponseEntity.noContent().build();
    }
}
