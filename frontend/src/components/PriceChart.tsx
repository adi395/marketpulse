import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts'
import type { PriceBar } from '../types/company'

export function PriceChart({ data }: { data: PriceBar[] }) {
  if (data.length === 0) {
    return <p className="text-sm text-neutral-500">No chart data available.</p>
  }

  const isUp = data[data.length - 1].close >= data[0].close
  const lineColor = isUp ? '#22c55e' : '#ef4444'

  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
          <CartesianGrid stroke="#262626" strokeDasharray="3 3" vertical={false} />
          <XAxis
            dataKey="date"
            tick={{ fill: '#737373', fontSize: 11 }}
            tickLine={false}
            axisLine={{ stroke: '#262626' }}
            minTickGap={40}
          />
          <YAxis
            domain={['auto', 'auto']}
            tick={{ fill: '#737373', fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            width={55}
            tickFormatter={(v) => `$${v}`}
          />
          <Tooltip
            contentStyle={{ backgroundColor: '#171717', border: '1px solid #262626', fontSize: 12 }}
            labelStyle={{ color: '#a3a3a3' }}
            formatter={(value) => [`$${Number(value).toFixed(2)}`, 'Close']}
          />
          <Line type="monotone" dataKey="close" stroke={lineColor} strokeWidth={1.5} dot={false} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}
