package com.marketpulse.backend.repository;

import com.marketpulse.backend.model.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findByUserIdOrderByTickerAsc(String userId);

    boolean existsByUserIdAndTicker(String userId, String ticker);

    /**
     * Deletes a user's own item. Because the delete is scoped by userId, it can
     * never remove another user's row; the returned count is how many rows the
     * caller actually owned and deleted (0 or 1).
     */
    long deleteByUserIdAndTicker(String userId, String ticker);
}
