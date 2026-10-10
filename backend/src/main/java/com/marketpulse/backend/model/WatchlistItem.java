package com.marketpulse.backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * One ticker saved to a user's watchlist. Rows are owned by {@code userId};
 * every query is scoped by it so users cannot see each other's lists. The
 * (userId, ticker) pair is unique so a ticker can't be added twice.
 */
@Entity
@Table(
        name = "watchlist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ticker"}),
        indexes = @Index(name = "idx_watchlist_user", columnList = "user_id")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String ticker;
}
