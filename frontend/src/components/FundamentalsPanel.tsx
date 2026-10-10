import type { Fundamentals } from '../types/company'
import { ratio, pct, money } from '../utils/format'

function Metric({ label, value, tone }: { label: string; value: string; tone?: 'up' | 'down' }) {
  const color =
    tone === 'up' ? 'text-green-500' : tone === 'down' ? 'text-red-500' : 'text-neutral-200'
  return (
    <div>
      <div className="text-xs text-neutral-500">{label}</div>
      <div className={`text-sm font-medium ${color}`}>{value}</div>
    </div>
  )
}

function Group({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div>
      <div className="mb-3 text-xs font-semibold uppercase tracking-wide text-neutral-400">
        {title}
      </div>
      <div className="grid grid-cols-2 gap-x-6 gap-y-4 sm:grid-cols-4">{children}</div>
    </div>
  )
}

function growthTone(value: number | null): 'up' | 'down' | undefined {
  if (value === null || value === undefined) return undefined
  return value >= 0 ? 'up' : 'down'
}

export function FundamentalsPanel({ data }: { data: Fundamentals | null }) {
  if (!data) {
    return <p className="text-sm text-neutral-500">No fundamentals available.</p>
  }

  return (
    <div className="space-y-6">
      <Group title="Valuation">
        <Metric label="P/E (TTM)" value={ratio(data.peTTM)} />
        <Metric label="P/E (Norm.)" value={ratio(data.peNormalized)} />
        <Metric label="Price / Sales" value={ratio(data.priceToSalesTTM)} />
        <Metric label="Price / Book" value={ratio(data.priceToBook)} />
      </Group>

      <Group title="Profitability">
        <Metric label="Gross Margin" value={pct(data.grossMarginTTM)} />
        <Metric label="Operating Margin" value={pct(data.operatingMarginTTM)} />
        <Metric label="Net Margin" value={pct(data.netMarginTTM)} />
        <Metric label="EPS (TTM)" value={money(data.epsTTM)} />
      </Group>

      <Group title="Growth & Leverage">
        <Metric label="EPS Growth (YoY)" value={pct(data.epsGrowthYoY)} tone={growthTone(data.epsGrowthYoY)} />
        <Metric
          label="Revenue Growth (YoY)"
          value={pct(data.revenueGrowthYoY)}
          tone={growthTone(data.revenueGrowthYoY)}
        />
        <Metric label="Debt / Equity" value={ratio(data.debtToEquity)} />
      </Group>
    </div>
  )
}
