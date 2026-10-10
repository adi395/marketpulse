import type { Earnings } from '../types/company'
import { money, pct } from '../utils/format'

export function EarningsTable({ earnings }: { earnings: Earnings[] }) {
  if (earnings.length === 0) {
    return <p className="text-sm text-ink-faint">No earnings history.</p>
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-border text-left text-[11px] uppercase tracking-wide text-ink-faint">
            <th className="py-2 pr-4 font-medium">Period</th>
            <th className="py-2 pr-4 font-medium">Qtr</th>
            <th className="py-2 pr-4 text-right font-medium">EPS Actual</th>
            <th className="py-2 pr-4 text-right font-medium">EPS Est.</th>
            <th className="py-2 text-right font-medium">Surprise</th>
          </tr>
        </thead>
        <tbody>
          {earnings.map((e) => {
            const beat = (e.surprisePercent ?? 0) >= 0
            return (
              <tr key={e.period} className="border-b border-border/60 last:border-0">
                <td className="nums py-2.5 pr-4 text-ink">{e.period}</td>
                <td className="py-2.5 pr-4 text-ink-muted">
                  Q{e.quarter} {e.year}
                </td>
                <td className="nums py-2.5 pr-4 text-right text-ink">{money(e.epsActual)}</td>
                <td className="nums py-2.5 pr-4 text-right text-ink-muted">{money(e.epsEstimate)}</td>
                <td
                  className="nums py-2.5 text-right font-medium"
                  style={{
                    color:
                      e.surprisePercent === null
                        ? 'var(--color-ink-faint)'
                        : beat
                          ? 'var(--color-up)'
                          : 'var(--color-down)',
                  }}
                >
                  {e.surprisePercent === null ? 'N/A' : `${beat ? '+' : ''}${pct(e.surprisePercent)}`}
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
