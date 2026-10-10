import { Link } from 'react-router-dom'
import type { MarketMover } from '../types/company'
import { money } from '../utils/format'

export function MarketCard({ mover }: { mover: MarketMover }) {
  const pc = mover.percentChange
  const isUp = (pc ?? 0) >= 0
  const changeColor = pc === null ? 'text-neutral-500' : isUp ? 'text-green-500' : 'text-red-500'
  const sign = isUp ? '+' : ''

  return (
    <Link
      to={`/company/${mover.ticker}`}
      className="block rounded border border-neutral-800 bg-neutral-900 p-4 transition-colors hover:border-neutral-600 hover:bg-neutral-800"
    >
      <div className="flex items-start justify-between">
        <div className="min-w-0">
          <div className="text-sm font-semibold text-neutral-100">{mover.ticker}</div>
          <div className="truncate text-xs text-neutral-500">{mover.name}</div>
        </div>
        <span className="shrink-0 text-xs text-neutral-600">{mover.sector}</span>
      </div>

      <div className="mt-4 flex items-baseline justify-between">
        <span className="text-lg font-semibold text-neutral-100">{money(mover.currentPrice)}</span>
        <span className={`text-sm font-medium ${changeColor}`}>
          {pc === null ? 'N/A' : `${sign}${pc.toFixed(2)}%`}
        </span>
      </div>
    </Link>
  )
}
