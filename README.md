# MarketPulse

A full-stack market research dashboard. Search any tracked company to see a live
quote, an interactive price chart, key fundamentals, recent news, earnings
history, and SEC filings — all assembled from external market-data APIs behind a
clean, cached backend.

**Stack:** React 19 + TypeScript + Vite · Spring Boot 4 (Java 21) · PostgreSQL ·
Caffeine caching · Docker Compose

---

## Screenshots

### Market overview dashboard
Tracked companies sorted by the day's move, with top gainer/loser highlights.

![Dashboard](docs/screenshots/dashboard.jpg)

### Company page — Overview
Live quote, an interactive 100-day price chart (Recharts), and grouped
fundamentals (valuation, profitability, growth).

![Company overview](docs/screenshots/company-overview.jpg)

### Company page — News, Earnings, Filings
Lazily-loaded tabs: a news feed, an EPS actual-vs-estimate table with beat/miss
coloring, and SEC filings linking out to EDGAR.

| News | Earnings | Filings |
| --- | --- | --- |
| ![News](docs/screenshots/company-news.jpg) | ![Earnings](docs/screenshots/company-earnings.jpg) | ![Filings](docs/screenshots/company-filings.jpg) |

---

## Features

- **Market overview dashboard** — every tracked company with its live quote,
  sorted by percent change, with top gainer / top loser highlights.
- **Company detail pages** with a tabbed layout:
  - **Overview** — live quote, day range, 52-week range, an interactive price
    chart, and grouped fundamentals.
  - **News** — recent company headlines with source and summary.
  - **Earnings** — quarterly EPS actual vs. estimate with surprise %.
  - **Filings** — recent SEC filings linking to EDGAR.
- **Resilient by design** — a rate-limited or failing data source degrades
  gracefully (e.g. an empty chart) instead of breaking the page.
- **Server-side caching** tuned per data type to stay within external API
  free-tier limits.

## Architecture

```
React + TypeScript (Vite)                Spring Boot (Java 21)
┌───────────────────────┐   HTTP/JSON    ┌──────────────────────────────────────┐
│  Dashboard             │ ─────────────► │  Controllers  (REST, /api/**)         │
│  Company page (tabs)   │                │       │                                │
│  Typed API client      │ ◄───────────── │  Services  (@Cacheable, Caffeine)     │
└───────────────────────┘                │       │                                │
                                          │  Providers  (interface per data type) │
                                          │       ├── Finnhub (quotes, fundamentals,│
                                          │       │     news, earnings, filings)    │
                                          │       └── Alpha Vantage (price charts)  │
                                          │  Repository / JPA  ──►  PostgreSQL      │
                                          └──────────────────────────────────────┘
```

Key design choices:

- **Provider abstraction.** Each data domain (quotes, charts, news, earnings,
  filings, fundamentals) is an interface with a vendor-specific implementation,
  so a provider can be swapped without touching controllers or services.
- **Per-cache TTLs.** Caffeine caches are tuned to how often each data source
  actually changes — 30s for quotes, 6h for daily charts, etc. — which keeps the
  app under the Finnhub and Alpha Vantage free-tier limits and makes repeat
  requests near-instant.
- **Layered backend.** Controller → Service → Provider → (RestClient / JPA),
  with DTOs at the boundary and a global exception handler mapping domain
  exceptions to HTTP responses.
- **Graceful degradation.** The frontend fetches the chart separately from core
  data so a throttled chart feed never blanks the company page.

## Tech stack

**Frontend**
- React 19, TypeScript, Vite 8
- React Router 7
- Tailwind CSS 4
- Recharts (price chart)
- Vitest 4 + Testing Library

**Backend**
- Java 21, Spring Boot 4 (Spring Web MVC)
- Spring Data JPA + PostgreSQL
- Spring Cache + Caffeine
- Spring `RestClient` for external APIs
- Lombok
- JUnit 5, Mockito, AssertJ, MockMvc, H2 (tests)

**Data sources:** [Finnhub](https://finnhub.io) · [Alpha Vantage](https://www.alphavantage.co)

**Infrastructure:** Docker, Docker Compose, nginx

## API

All endpoints are under `/api`.

| Method | Path | Description |
| --- | --- | --- |
| GET | `/market/overview` | All tracked companies with quotes, sorted by % change |
| GET | `/companies/{ticker}` | Company profile |
| GET | `/companies/{ticker}/quote` | Latest quote |
| GET | `/companies/{ticker}/chart` | Historical daily price bars |
| GET | `/companies/{ticker}/fundamentals` | Valuation / profitability / growth metrics |
| GET | `/companies/{ticker}/news` | Recent company news |
| GET | `/companies/{ticker}/earnings` | Quarterly earnings history |
| GET | `/companies/{ticker}/filings` | Recent SEC filings |

## Getting started

### Prerequisites
- Free API keys from [Finnhub](https://finnhub.io) and [Alpha Vantage](https://www.alphavantage.co)

### Run with Docker (recommended)

```bash
cp .env.example .env     # add your FINNHUB_API_KEY and ALPHAVANTAGE_API_KEY
docker compose up --build
```

Then open http://localhost:5173. Compose starts Postgres, the backend (port
8080), and the nginx-served frontend (port 5173, proxying `/api` to the backend).

### Run locally (without Docker)

Requirements: Java 21, Node 20+, and a local PostgreSQL with a `marketpulse`
database.

```bash
# backend
cd backend
export FINNHUB_API_KEY=... ALPHAVANTAGE_API_KEY=...
./mvnw spring-boot:run        # http://localhost:8080

# frontend (in a second terminal)
cd frontend
npm install
npm run dev                   # http://localhost:5173
```

The backend seeds ~13 large-cap companies on first run.

## Testing

```bash
# backend — JUnit 5 + Mockito + MockMvc, runs against in-memory H2
cd backend && ./mvnw test

# frontend — Vitest + Testing Library
cd frontend && npm test
```
