package com.marketpulse.backend.controller;

import com.marketpulse.backend.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WatchlistService watchlistService;

    @Test
    void listRequiresUserIdHeader() throws Exception {
        mockMvc.perform(get("/api/watchlist"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsForTheHeaderUser() throws Exception {
        when(watchlistService.getWatchlist("user-123")).thenReturn(List.of());

        mockMvc.perform(get("/api/watchlist").header("X-User-Id", "user-123"))
                .andExpect(status().isOk());

        verify(watchlistService).getWatchlist("user-123");
    }

    @Test
    void addUsesTheHeaderUserAndPathTicker() throws Exception {
        mockMvc.perform(post("/api/watchlist/AAPL").header("X-User-Id", "user-123"))
                .andExpect(status().isCreated());

        verify(watchlistService).add(eq("user-123"), eq("AAPL"));
    }

    @Test
    void removeUsesTheHeaderUserAndPathTicker() throws Exception {
        mockMvc.perform(delete("/api/watchlist/AAPL").header("X-User-Id", "user-123"))
                .andExpect(status().isNoContent());

        verify(watchlistService).remove(eq("user-123"), eq("AAPL"));
    }

    @Test
    void rejectsMalformedUserId() throws Exception {
        mockMvc.perform(get("/api/watchlist").header("X-User-Id", "bad id!!"))
                .andExpect(status().isBadRequest());
    }
}
