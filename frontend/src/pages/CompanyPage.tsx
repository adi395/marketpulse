import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../services/api'
import type { Company, Quote, Fundamentals } from '../types/company'

export function CompanyPage() {
  const { ticker } = useParams<{ ticker: string }>()
  const [company, setCompany] = useState<Company | null>(null)
  const [quote, setQuote] = useState<Quote | null>(null)
  const [fundamentals, setFundamentals] = useState<Fundamentals | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!ticker) return
    setLoading(true)
    setError(null)

    Promise.all([
      api.getCompany(ticker),
      api.getQuote(ticker),
      api.getFundamentals(ticker),
    ])
      .then(([companyData, quoteData, fundamentalsData]) => {
        setCompany(companyData)
        setQuote(quoteData)
        setFundamentals(fundamentalsData)
      })
      .catch(() => setError(`Could not load data for ${ticker}`))
      .finally(() => setLoading(false))
  }, [ticker])

  if (loading) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-6">
        <p className="text-sm text-neutral-500">Loading {ticker}...</p>
      </div>
    )
  }

  if (error || !company || !quote) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-6">
        <p className="text-sm text-red-400">{error ?? 'Something went wrong.'}</p>
      </div>
    )
  }

  const isUp = quote.change >= 0
  const changeColor = isUp ? 'text-green-500' : 'text-red-500'
  const sign = isUp ? '+' : ''

  return (
    <div className="mx-auto max-w-7xl px-4 py-6">
      <div className="mb-1 text-xs uppercase tracking-wide text-neutral-500">
        {company.exchange} · {company.sector}
      </div>
      <h1 className="text-xl font-semibold text-neutral-100">{company.name}</h1>
      <div className="mb-6 text-sm text-neutral-500">{company.ticker}</div>

      <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
        <div className="flex items-baseline gap-3">
          <span className="text-3xl font-semibold text-neutral-100">
            ${quote.currentPrice.toFixed(2)}
          </span>
          <span className={`text-sm font-medium ${changeColor}`}>
            {sign}{quote.change.toFixed(2)} ({sign}{quote.percentChange.toFixed(2)}%)
          </span>
        </div>

        <div className="mt-4 grid grid-cols-4 gap-4 border-t border-neutral-800 pt-4 text-sm">
          <div>
            <div className="text-neutral-500">Day High</div>
            <div className="text-neutral-200">${quote.dayHigh.toFixed(2)}</div>
          </div>
          <div>
            <div className="text-neutral-500">Day Low</div>
            <div className="text-neutral-200">${quote.dayLow.toFixed(2)}</div>
          </div>
          <div>
            <div className="text-neutral-500">52W High</div>
            <div className="text-neutral-200">
              {fundamentals?.week52High ? `$${fundamentals.week52High.toFixed(2)}` : 'N/A'}
            </div>
          </div>
          <div>
            <div className="text-neutral-500">52W Low</div>
            <div className="text-neutral-200">
              {fundamentals?.week52Low ? `$${fundamentals.week52Low.toFixed(2)}` : 'N/A'}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
