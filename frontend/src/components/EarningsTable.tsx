import type { Earnings } from '../types/company'
import { money, pct } from '../utils/format'

export function EarningsTable({ earnings }: { earnings: Earnings[] }) {
  if (earnings.length === 0) {
    return <p className="text-sm text-neutral-500">No earnings history.</p>
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-neutral-800 text-left text-xs uppercase tracking-wide text-neutral-500">
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
              <tr key={e.period} className="border-b border-neutral-800/60">
                <td className="py-2.5 pr-4 text-neutral-300">{e.period}</td>
                <td className="py-2.5 pr-4 text-neutral-500">
                  Q{e.quarter} {e.year}
                </td>
                <td className="py-2.5 pr-4 text-right text-neutral-200">{money(e.epsActual)}</td>
                <td className="py-2.5 pr-4 text-right text-neutral-400">{money(e.epsEstimate)}</td>
                <td
                  className={`py-2.5 text-right font-medium ${
                    e.surprisePercent === null ? 'text-neutral-500' : beat ? 'text-green-500' : 'text-red-500'
                  }`}
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
