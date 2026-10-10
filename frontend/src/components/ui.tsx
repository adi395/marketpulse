import { money } from '../utils/format'

/** Colored, tinted pill showing a percent change with a direction arrow. */
export function ChangePill({
  percent,
  size = 'md',
}: {
  percent: number | null
  size?: 'sm' | 'md'
}) {
  if (percent === null || percent === undefined) {
    return <span className="text-ink-faint">N/A</span>
  }
  const up = percent >= 0
  const pad = size === 'sm' ? 'px-1.5 py-0.5 text-xs' : 'px-2 py-0.5 text-sm'
  return (
    <span
      className={`nums inline-flex items-center gap-0.5 rounded-md font-medium ${pad}`}
      style={{
        color: up ? 'var(--color-up)' : 'var(--color-down)',
        backgroundColor: up ? 'color-mix(in srgb, var(--color-up) 14%, transparent)' : 'color-mix(in srgb, var(--color-down) 14%, transparent)',
      }}
    >
      <span aria-hidden="true">{up ? '▲' : '▼'}</span>
      {up ? '+' : ''}
      {percent.toFixed(2)}%
    </span>
  )
}

/**
 * Thin track showing where the current price sits between the day's low and
 * high. Returns null when the range is unavailable so callers never render a
 * misleading empty bar.
 */
export function DayRangeBar({
  low,
  high,
  current,
  label = "Day range",
}: {
  low: number | null
  high: number | null
  current: number | null
  label?: string
}) {
  if (low === null || high === null || current === null || high <= low) return null
  const pct = Math.min(100, Math.max(0, ((current - low) / (high - low)) * 100))
  return (
    <div>
      <div className="mb-1 text-[10px] uppercase tracking-wide text-ink-faint">{label}</div>
      <div
        className="relative h-1 w-full rounded-full bg-surface-2"
        role="img"
        aria-label={`${label}: ${money(low)} to ${money(high)}, currently ${money(current)}`}
      >
        <div
          className="absolute top-1/2 h-2 w-2 -translate-x-1/2 -translate-y-1/2 rounded-full"
          style={{ left: `${pct}%`, backgroundColor: 'var(--color-accent)' }}
        />
      </div>
      <div className="nums mt-1.5 flex justify-between text-[11px] text-ink-muted">
        <span>{money(low)}</span>
        <span>{money(high)}</span>
      </div>
    </div>
  )
}

/** Shimmering placeholder block. */
export function Skeleton({ className = '' }: { className?: string }) {
  return <div className={`animate-pulse rounded-md bg-surface-2 ${className}`} />
}
