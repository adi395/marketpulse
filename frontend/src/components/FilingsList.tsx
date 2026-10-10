import type { SecFiling } from '../types/company'
import { shortDate } from '../utils/format'

export function FilingsList({ filings }: { filings: SecFiling[] }) {
  if (filings.length === 0) {
    return <p className="text-sm text-ink-faint">No recent filings.</p>
  }

  return (
    <ul className="divide-y divide-border">
      {filings.map((f, i) => (
        <li key={`${f.filingUrl}-${i}`}>
          <a
            href={f.filingUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="group flex items-center justify-between py-3 first:pt-0 last:pb-0"
          >
            <div className="flex items-center gap-3">
              <span className="inline-block min-w-[3.5rem] rounded-md border border-border bg-surface-2 px-2 py-0.5 text-center text-xs font-medium text-ink">
                {f.formType}
              </span>
              <span className="text-sm text-ink-muted transition-colors group-hover:text-ink">
                Filed {shortDate(f.filedDate)}
              </span>
            </div>
            <span className="text-xs text-ink-faint transition-colors group-hover:text-accent-soft">
              View ↗
            </span>
          </a>
        </li>
      ))}
    </ul>
  )
}
