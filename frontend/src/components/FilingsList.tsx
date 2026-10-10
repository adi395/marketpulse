import type { SecFiling } from '../types/company'
import { shortDate } from '../utils/format'

export function FilingsList({ filings }: { filings: SecFiling[] }) {
  if (filings.length === 0) {
    return <p className="text-sm text-neutral-500">No recent filings.</p>
  }

  return (
    <ul className="divide-y divide-neutral-800">
      {filings.map((f, i) => (
        <li key={`${f.filingUrl}-${i}`}>
          <a
            href={f.filingUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="group flex items-center justify-between py-3 first:pt-0 last:pb-0"
          >
            <div className="flex items-center gap-3">
              <span className="inline-block rounded border border-neutral-700 px-2 py-0.5 text-xs font-medium text-neutral-300">
                {f.formType}
              </span>
              <span className="text-sm text-neutral-400 group-hover:text-neutral-200">
                Filed {shortDate(f.filedDate)}
              </span>
            </div>
            <span className="text-xs text-neutral-600 group-hover:text-neutral-400">View ↗</span>
          </a>
        </li>
      ))}
    </ul>
  )
}
