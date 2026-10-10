import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { MarketCard } from '../components/MarketCard'
import { ChangePill, Skeleton } from '../components/ui'
import type { MarketMover } from '../types/company'

function StatTile({
  label,
  children,
}: {
  label: string
  children: React.ReactNode
}) {
  return (
    <div className="rounded-xl border border-border bg-surface px-4 py-3">
      <div className="text-[11px] font-medium uppercase tracking-wide text-ink-faint">{label}</div>
      <div className="mt-1.5">{children}</div>
    </div>
  )
}

export function Dashboard() {
  const [movers, setMovers] = useState<MarketMover[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    api
      .getMarketOverview()
      .then(setMovers)
      .catch(() => setError('Could not load market overview.'))
  }, [])

  const withChange = (movers ?? []).filter((m) => m.percentChange !== null)
  const advancing = withChange.filter((m) => (m.percentChange ?? 0) > 0).length
  const declining = withChange.filter((m) => (m.percentChange ?? 0) < 0).length
  const topGainer = withChange.length ? withChange[0] : null
  const topLoser = withChange.length ? withChange[withChange.length - 1] : null

  return (
    <div className="mx-auto max-w-7xl px-5 py-8">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-ink">Market Overview</h1>
          <p className="mt-1 text-sm text-ink-muted">
            Tracked companies, sorted by today&apos;s move.
          </p>
        </div>
        {movers && (
          <div className="hidden text-right text-xs text-ink-faint sm:block">
            {movers.length} companies tracked
          </div>
        )}
      </div>

      {error && (
        <div className="mt-6 rounded-xl border border-border bg-surface p-4 text-sm text-ink-muted">
          {error}
        </div>
      )}

      {/* Stat row */}
      {movers === null && !error ? (
        <div className="mt-6 grid grid-cols-2 gap-3 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-[72px]" />
          ))}
        </div>
      ) : movers && movers.length > 0 ? (
        <div className="mt-6 grid grid-cols-2 gap-3 lg:grid-cols-4">
          <StatTile label="Advancing">
            <span className="nums text-lg font-semibold" style={{ color: 'var(--color-up)' }}>
              {advancing}
            </span>
          </StatTile>
          <StatTile label="Declining">
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
      <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {movers === null && !error
          ? Array.from({ length: 8 }).map((_, i) => <Skeleton key={i} className="h-[148px]" />)
          : movers?.map((m) => <MarketCard key={m.ticker} mover={m} />)}
      </div>
    </div>
  )
}
