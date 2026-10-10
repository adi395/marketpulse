import { useEffect, useMemo, useState } from 'react'
import { api } from '../services/api'
import { MarketCard } from '../components/MarketCard'
import { ChangePill, Skeleton } from '../components/ui'
import { timeAgo } from '../utils/format'
import type { MarketOverview, MarketMover } from '../types/company'

type SortField = 'change' | 'price' | 'ticker' | 'name'
type SortDir = 'asc' | 'desc'

const SORT_LABELS: Record<SortField, string> = {
  change: 'Change %',
  price: 'Price',
  ticker: 'Ticker',
  name: 'Name',
}

function sortValue(m: MarketMover, field: SortField): number | string {
  switch (field) {
    case 'change':
      return m.percentChange ?? Number.NEGATIVE_INFINITY
    case 'price':
      return m.currentPrice ?? Number.NEGATIVE_INFINITY
    case 'ticker':
      return m.ticker
    case 'name':
      return m.name
  }
}

function StatTile({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="rounded-xl border border-border bg-surface px-4 py-3">
      <div className="text-[11px] font-medium uppercase tracking-wide text-ink-faint">{label}</div>
      <div className="mt-1.5">{children}</div>
    </div>
  )
}

const selectClass =
  'rounded-lg border border-border bg-surface px-3 py-1.5 text-sm text-ink outline-none transition-colors focus:border-accent/70 focus-visible:ring-2 focus-visible:ring-accent/40'

export function Dashboard() {
  const [overview, setOverview] = useState<MarketOverview | null>(null)
  const [error, setError] = useState<string | null>(null)

  const [sector, setSector] = useState('all')
  const [sortField, setSortField] = useState<SortField>('change')
  const [sortDir, setSortDir] = useState<SortDir>('desc')

  useEffect(() => {
    api
      .getMarketOverview()
      .then(setOverview)
      .catch(() => setError('Could not load market overview. The data provider may be unavailable.'))
  }, [])

  const companies = overview?.companies ?? []

  const sectors = useMemo(
    () => Array.from(new Set(companies.map((c) => c.sector).filter(Boolean))).sort(),
    [companies],
  )

  const visible = useMemo(() => {
    const filtered = sector === 'all' ? companies : companies.filter((c) => c.sector === sector)
    const factor = sortDir === 'asc' ? 1 : -1
    return [...filtered].sort((a, b) => {
      const av = sortValue(a, sortField)
      const bv = sortValue(b, sortField)
      if (typeof av === 'string' && typeof bv === 'string') return factor * av.localeCompare(bv)
      return factor * ((av as number) - (bv as number))
    })
  }, [companies, sector, sortField, sortDir])

  // Advancing/declining are scoped to the companies currently in view.
  const advancing = visible.filter((m) => (m.percentChange ?? 0) > 0).length
  const declining = visible.filter((m) => (m.percentChange ?? 0) < 0).length
  const byChange = [...visible].sort((a, b) => (b.percentChange ?? -Infinity) - (a.percentChange ?? -Infinity))
  const topGainer = byChange[0] ?? null
  const topLoser = byChange[byChange.length - 1] ?? null

  const loading = overview === null && !error

  return (
    <div className="mx-auto max-w-7xl px-5 py-8">
      {/* Header */}
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-ink">Market Overview</h1>
          <div className="mt-1.5 flex flex-wrap items-center gap-x-2 gap-y-1 text-sm text-ink-muted">
            {overview ? (
              <>
                <span>
                  Source: <span className="text-ink">{overview.source}</span>
                </span>
                <span className="text-ink-faint">·</span>
                <span>Updated {timeAgo(overview.asOf)}</span>
                <span
                  className="rounded-md border border-border px-1.5 py-0.5 text-[11px] font-medium text-ink-muted"
                  title="Live quotes, cached up to 30s and subject to exchange delay. Not real-time trading data."
                >
                  Delayed
                </span>
              </>
            ) : (
              <span>Live quotes across tracked companies.</span>
            )}
          </div>
        </div>

        {/* Controls */}
        {overview && companies.length > 0 && (
          <div className="flex flex-wrap items-center gap-2">
            <label className="sr-only" htmlFor="sector">
              Filter by sector
            </label>
            <select
              id="sector"
              className={selectClass}
              value={sector}
              onChange={(e) => setSector(e.target.value)}
            >
              <option value="all">All sectors</option>
              {sectors.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>

            <label className="sr-only" htmlFor="sort">
              Sort by
            </label>
            <select
              id="sort"
              className={selectClass}
              value={sortField}
              onChange={(e) => setSortField(e.target.value as SortField)}
            >
              {(Object.keys(SORT_LABELS) as SortField[]).map((f) => (
                <option key={f} value={f}>
                  Sort: {SORT_LABELS[f]}
                </option>
              ))}
            </select>

            <button
              type="button"
              onClick={() => setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'))}
              aria-label={`Sort ${sortDir === 'asc' ? 'ascending' : 'descending'}`}
              className="rounded-lg border border-border bg-surface px-3 py-1.5 text-sm text-ink-muted transition-colors hover:text-ink focus:outline-none focus-visible:ring-2 focus-visible:ring-accent/40"
            >
              {sortDir === 'asc' ? '↑' : '↓'}
            </button>
          </div>
        )}
      </div>

      {error && (
        <div className="mt-6 rounded-xl border border-border bg-surface p-5 text-sm text-ink-muted">
          {error}
        </div>
      )}

      {/* Stat row */}
      {loading ? (
        <div className="mt-6 grid grid-cols-2 gap-3 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-[72px]" />
          ))}
        </div>
      ) : overview && companies.length > 0 ? (
        <div className="mt-6 grid grid-cols-2 gap-3 lg:grid-cols-4">
          <StatTile label={`Advancing (of ${visible.length})`}>
            <span className="nums text-lg font-semibold" style={{ color: 'var(--color-up)' }}>
              {advancing}
            </span>
          </StatTile>
          <StatTile label={`Declining (of ${visible.length})`}>
            <span className="nums text-lg font-semibold" style={{ color: 'var(--color-down)' }}>
              {declining}
            </span>
          </StatTile>
          <StatTile label="Top Gainer">
            <div className="flex items-center gap-2">
              <span className="text-sm font-semibold text-ink">{topGainer?.ticker ?? '—'}</span>
              <ChangePill percent={topGainer?.percentChange ?? null} size="sm" />
            </div>
          </StatTile>
          <StatTile label="Top Loser">
            <div className="flex items-center gap-2">
              <span className="text-sm font-semibold text-ink">{topLoser?.ticker ?? '—'}</span>
              <ChangePill percent={topLoser?.percentChange ?? null} size="sm" />
            </div>
          </StatTile>
        </div>
      ) : null}

      {/* Card grid */}
      {loading ? (
        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <Skeleton key={i} className="h-[168px]" />
          ))}
        </div>
      ) : overview && visible.length > 0 ? (
        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {visible.map((m) => (
            <MarketCard key={m.ticker} mover={m} />
          ))}
        </div>
      ) : overview && companies.length > 0 ? (
        <div className="mt-6 rounded-xl border border-border bg-surface p-8 text-center text-sm text-ink-muted">
          No companies match this filter.
        </div>
      ) : overview ? (
        <div className="mt-6 rounded-xl border border-border bg-surface p-8 text-center text-sm text-ink-muted">
          No market data available right now.
        </div>
      ) : null}
    </div>
  )
}
