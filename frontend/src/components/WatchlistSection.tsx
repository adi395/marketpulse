import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../services/api'
import { ChangePill, Skeleton } from './ui'
import { money } from '../utils/format'
import type { MarketMover } from '../types/company'

export function WatchlistSection() {
  const [items, setItems] = useState<MarketMover[] | null>(null)
  const [error, setError] = useState(false)

  useEffect(() => {
    api.getWatchlist().then(setItems).catch(() => setError(true))
  }, [])

  function remove(ticker: string) {
    setItems((prev) => prev?.filter((i) => i.ticker !== ticker) ?? prev)
    api.removeFromWatchlist(ticker).catch(() => {
      // Re-sync on failure so the UI reflects the server.
      api.getWatchlist().then(setItems).catch(() => setError(true))
    })
  }

  return (
    <section className="mb-10">
      <h2 className="text-lg font-semibold tracking-tight text-ink">Your Watchlist</h2>

      {error ? (
        <p className="mt-3 text-sm text-ink-muted">Your watchlist is temporarily unavailable.</p>
      ) : items === null ? (
        <Skeleton className="mt-3 h-24" />
      ) : items.length === 0 ? (
        <p className="mt-3 text-sm text-ink-muted">
          Your watchlist is empty. Open a company and tap{' '}
          <span className="text-ink">★ Watchlist</span> to add it.
        </p>
      ) : (
        <ul className="mt-3 divide-y divide-border rounded-xl border border-border bg-surface px-4">
          {items.map((m) => (
            <li key={m.ticker} className="flex items-center gap-3 py-3">
              <Link
                to={`/company/${m.ticker}`}
                className="flex min-w-0 flex-1 items-center gap-3 rounded focus:outline-none focus-visible:ring-2 focus-visible:ring-accent/40"
              >
                <span className="w-14 shrink-0 text-sm font-semibold text-ink">{m.ticker}</span>
                <span className="truncate text-sm text-ink-muted">{m.name}</span>
              </Link>
              <span className="nums text-sm text-ink">{money(m.currentPrice)}</span>
              <ChangePill percent={m.percentChange} size="sm" />
              <button
                type="button"
                onClick={() => remove(m.ticker)}
                aria-label={`Remove ${m.ticker} from watchlist`}
                className="rounded-md px-2 py-1 text-ink-faint transition-colors hover:text-ink focus:outline-none focus-visible:ring-2 focus-visible:ring-accent/40"
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
