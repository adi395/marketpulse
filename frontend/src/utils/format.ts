export function num(value: number | null | undefined, digits = 2): string {
  if (value === null || value === undefined || Number.isNaN(value)) return 'N/A'
  return value.toFixed(digits)
}

export function ratio(value: number | null | undefined): string {
  if (value === null || value === undefined || Number.isNaN(value)) return 'N/A'
  return `${value.toFixed(2)}x`
}

export function pct(value: number | null | undefined): string {
  if (value === null || value === undefined || Number.isNaN(value)) return 'N/A'
  return `${value.toFixed(2)}%`
}

export function money(value: number | null | undefined): string {
  if (value === null || value === undefined || Number.isNaN(value)) return 'N/A'
  return `$${value.toFixed(2)}`
}

/** Formats a unix-seconds timestamp as a short readable date. */
export function fromUnix(seconds: number): string {
  return new Date(seconds * 1000).toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

/** Human "time ago" from a unix-seconds timestamp, e.g. "just now", "4m ago". */
export function timeAgo(seconds: number | null | undefined, now = Date.now()): string {
  if (seconds === null || seconds === undefined) return 'unknown'
  const diffSec = Math.max(0, Math.round(now / 1000 - seconds))
  if (diffSec < 45) return 'just now'
  if (diffSec < 3600) return `${Math.round(diffSec / 60)}m ago`
  if (diffSec < 86400) return `${Math.round(diffSec / 3600)}h ago`
  return `${Math.round(diffSec / 86400)}d ago`
}

/** Normalizes the backend's "YYYY-MM-DD HH:mm:ss" / "YYYY-MM-DD" into a short date. */
export function shortDate(value: string): string {
  const parsed = new Date(value.replace(' ', 'T'))
  if (Number.isNaN(parsed.getTime())) return value
  return parsed.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}
