import { Link } from 'react-router-dom'
import type { MarketMover } from '../types/company'
import { money } from '../utils/format'
import { ChangePill, DayRangeBar } from './ui'

export function MarketCard({ mover }: { mover: MarketMover }) {
  const up = (mover.percentChange ?? 0) >= 0

  return (
    <Link
      to={`/company/${mover.ticker}`}
      className="group relative block overflow-hidden rounded-xl border border-border bg-surface p-4 transition-all duration-150 hover:-translate-y-0.5 hover:border-border-strong hover:bg-surface-2 hover:shadow-lg hover:shadow-black/30"
    >
      {/* Accent edge tinted by direction */}
      <span
        className="absolute inset-y-0 left-0 w-0.5 opacity-70"
        style={{ backgroundColor: up ? 'var(--color-up)' : 'var(--color-down)' }}
      />

      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0">
          <div className="text-sm font-semibold tracking-tight text-ink">{mover.ticker}</div>
          <div className="truncate text-xs text-ink-muted">{mover.name}</div>
        </div>
        <span className="shrink-0 rounded border border-border px-1.5 py-0.5 text-[10px] uppercase tracking-wide text-ink-faint">
          {mover.sector}
        </span>
      </div>

      <div className="mt-4 flex items-end justify-between">
        <span className="nums text-xl font-semibold text-ink">{money(mover.currentPrice)}</span>
        <ChangePill percent={mover.percentChange} size="sm" />
      </div>

      <div className="mt-4">
        <DayRangeBar low={mover.dayLow} high={mover.dayHigh} current={mover.currentPrice} />
      </div>
    </Link>
  )
}
