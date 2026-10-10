import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'
import type { PriceBar } from '../types/company'

const UP = '#1fc686'
const DOWN = '#f05a68'

export function PriceChart({ data }: { data: PriceBar[] }) {
  if (data.length === 0) {
    return (
      <div className="flex h-72 items-center justify-center text-sm text-ink-faint">
        No chart data available.
      </div>
    )
  }

  const isUp = data[data.length - 1].close >= data[0].close
  const color = isUp ? UP : DOWN

  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 8, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="priceFill" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={color} stopOpacity={0.28} />
              <stop offset="100%" stopColor={color} stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid stroke="#232a34" strokeDasharray="3 3" vertical={false} />
          <XAxis
            dataKey="date"
            tick={{ fill: '#626c7c', fontSize: 11 }}
            tickLine={false}
            axisLine={{ stroke: '#232a34' }}
            minTickGap={44}
          />
          <YAxis
            domain={['auto', 'auto']}
            tick={{ fill: '#626c7c', fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            width={56}
            tickFormatter={(v) => `$${v}`}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: '#191d25',
              border: '1px solid #333c49',
              borderRadius: 8,
              fontSize: 12,
            }}
            labelStyle={{ color: '#96a1b2', marginBottom: 2 }}
            itemStyle={{ color: '#e9edf3' }}
            formatter={(value) => [`$${Number(value).toFixed(2)}`, 'Close']}
          />
          <Area
            type="monotone"
            dataKey="close"
            stroke={color}
            strokeWidth={2}
            fill="url(#priceFill)"
            dot={false}
            activeDot={{ r: 3, strokeWidth: 0 }}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
