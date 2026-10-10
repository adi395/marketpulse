package com.marketpulse.backend.repository;

import com.marketpulse.backend.model.WatchlistItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WatchlistRepositoryTest {

    @Autowired
    private WatchlistRepository repo;

    private void save(String userId, String ticker) {
        repo.save(WatchlistItem.builder().userId(userId).ticker(ticker).build());
    }

    @Test
    void listsOnlyTheRequestingUsersItems() {
        save("userA", "AAPL");
        save("userA", "MSFT");
        save("userB", "NVDA");

        assertThat(repo.findByUserIdOrderByTickerAsc("userA"))
                .extracting(WatchlistItem::getTicker)
                .containsExactly("AAPL", "MSFT");
        assertThat(repo.findByUserIdOrderByTickerAsc("userB"))
                .extracting(WatchlistItem::getTicker)
                .containsExactly("NVDA");
    }

    @Test
    void existsIsScopedByUser() {
        save("userA", "AAPL");

        assertThat(repo.existsByUserIdAndTicker("userA", "AAPL")).isTrue();
        assertThat(repo.existsByUserIdAndTicker("userB", "AAPL")).isFalse();
    }

    @Test
    void oneUserCannotDeleteAnothersItem() {
        save("userA", "AAPL");

        long deletedByB = repo.deleteByUserIdAndTicker("userB", "AAPL");

        assertThat(deletedByB).isZero();
        assertThat(repo.existsByUserIdAndTicker("userA", "AAPL")).isTrue();
    }

    @Test
    void deletingOwnItemRemovesExactlyOneRow() {
        save("userA", "AAPL");

        assertThat(repo.deleteByUserIdAndTicker("userA", "AAPL")).isEqualTo(1);
        assertThat(repo.existsByUserIdAndTicker("userA", "AAPL")).isFalse();
    }
}
