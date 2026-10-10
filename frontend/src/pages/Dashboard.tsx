import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { MarketCard } from '../components/MarketCard'
import type { MarketMover } from '../types/company'

function Highlight({ label, mover }: { label: string; mover: MarketMover | null }) {
  if (!mover || mover.percentChange === null) return null
  const isUp = mover.percentChange >= 0
  const color = isUp ? 'text-green-500' : 'text-red-500'
  const sign = isUp ? '+' : ''
  return (
    <div className="rounded border border-neutral-800 bg-neutral-900 px-4 py-3">
      <div className="text-xs uppercase tracking-wide text-neutral-500">{label}</div>
      <div className="mt-1 flex items-baseline gap-2">
        <span className="text-sm font-semibold text-neutral-100">{mover.ticker}</span>
        <span className={`text-sm font-medium ${color}`}>
          {sign}
          {mover.percentChange.toFixed(2)}%
        </span>
      </div>
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
  const topGainer = withChange.length ? withChange[0] : null
  const topLoser = withChange.length ? withChange[withChange.length - 1] : null

  return (
    <div className="mx-auto max-w-7xl px-4 py-6">
      <h1 className="text-lg font-semibold text-neutral-100">Market Overview</h1>
      <p className="mt-1 text-sm text-neutral-500">
        Tracked companies, sorted by today&apos;s move. Click any card for the full breakdown.
      </p>

      {error && <p className="mt-6 text-sm text-red-400">{error}</p>}

      {movers === null && !error && (
        <p className="mt-6 text-sm text-neutral-500">Loading market data...</p>
      )}

      {movers && movers.length > 0 && (
        <>
          <div className="mt-6 grid grid-cols-2 gap-4 sm:max-w-md">
            <Highlight label="Top Gainer" mover={topGainer} />
            <Highlight label="Top Loser" mover={topLoser} />
          </div>

          <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {movers.map((m) => (
              <MarketCard key={m.ticker} mover={m} />
            ))}
          </div>
        </>
      )}
    </div>
  )
}
