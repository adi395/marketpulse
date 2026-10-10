import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../services/api'
import { PriceChart } from '../components/PriceChart'
import { FundamentalsPanel } from '../components/FundamentalsPanel'
import { NewsList } from '../components/NewsList'
import { EarningsTable } from '../components/EarningsTable'
import { FilingsList } from '../components/FilingsList'
import { money } from '../utils/format'
import type {
  Company,
  Quote,
  Fundamentals,
  PriceBar,
  NewsArticle,
  Earnings,
  SecFiling,
} from '../types/company'

type Tab = 'overview' | 'news' | 'earnings' | 'filings'

const TABS: { id: Tab; label: string }[] = [
  { id: 'overview', label: 'Overview' },
  { id: 'news', label: 'News' },
  { id: 'earnings', label: 'Earnings' },
  { id: 'filings', label: 'Filings' },
]

export function CompanyPage() {
  const { ticker } = useParams<{ ticker: string }>()
  const [company, setCompany] = useState<Company | null>(null)
  const [quote, setQuote] = useState<Quote | null>(null)
  const [fundamentals, setFundamentals] = useState<Fundamentals | null>(null)
  const [chart, setChart] = useState<PriceBar[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [tab, setTab] = useState<Tab>('overview')
  const [news, setNews] = useState<NewsArticle[] | null>(null)
  const [earnings, setEarnings] = useState<Earnings[] | null>(null)
  const [filings, setFilings] = useState<SecFiling[] | null>(null)

  // Core data: company, quote and fundamentals must load for the page to render.
  useEffect(() => {
    if (!ticker) return
    setLoading(true)
    setError(null)
    setTab('overview')
    setNews(null)
    setEarnings(null)
    setFilings(null)

    Promise.all([api.getCompany(ticker), api.getQuote(ticker), api.getFundamentals(ticker)])
      .then(([companyData, quoteData, fundamentalsData]) => {
        setCompany(companyData)
        setQuote(quoteData)
        setFundamentals(fundamentalsData)
      })
      .catch(() => setError(`Could not load data for ${ticker}`))
      .finally(() => setLoading(false))

    // Chart is best-effort: a failing/rate-limited chart feed should not
    // blank out the company, quote and fundamentals that loaded fine.
    api.getChart(ticker).then(setChart).catch(() => setChart([]))
  }, [ticker])

  // Tab data is fetched lazily the first time each tab is opened.
  useEffect(() => {
    if (!ticker) return
    if (tab === 'news' && news === null) {
      api.getNews(ticker).then(setNews).catch(() => setNews([]))
    } else if (tab === 'earnings' && earnings === null) {
      api.getEarnings(ticker).then(setEarnings).catch(() => setEarnings([]))
    } else if (tab === 'filings' && filings === null) {
      api.getFilings(ticker).then(setFilings).catch(() => setFilings([]))
    }
  }, [tab, ticker, news, earnings, filings])

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
            {money(quote.currentPrice)}
          </span>
          <span className={`text-sm font-medium ${changeColor}`}>
            {sign}
            {quote.change.toFixed(2)} ({sign}
            {quote.percentChange.toFixed(2)}%)
          </span>
        </div>

        <div className="mt-4 grid grid-cols-2 gap-4 border-t border-neutral-800 pt-4 text-sm sm:grid-cols-4">
          <div>
            <div className="text-neutral-500">Day High</div>
            <div className="text-neutral-200">{money(quote.dayHigh)}</div>
          </div>
          <div>
            <div className="text-neutral-500">Day Low</div>
            <div className="text-neutral-200">{money(quote.dayLow)}</div>
          </div>
          <div>
            <div className="text-neutral-500">52W High</div>
            <div className="text-neutral-200">{money(fundamentals?.week52High)}</div>
          </div>
          <div>
            <div className="text-neutral-500">52W Low</div>
            <div className="text-neutral-200">{money(fundamentals?.week52Low)}</div>
          </div>
        </div>
      </div>

      <div className="mt-6 flex gap-1 border-b border-neutral-800">
        {TABS.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className={`-mb-px border-b-2 px-4 py-2 text-sm transition-colors ${
              tab === t.id
                ? 'border-neutral-100 text-neutral-100'
                : 'border-transparent text-neutral-500 hover:text-neutral-300'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      <div className="mt-6">
        {tab === 'overview' && (
          <div className="space-y-6">
            <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
              <div className="mb-4 text-sm text-neutral-400">Price Chart</div>
              <PriceChart data={chart} />
            </div>
            <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
              <div className="mb-4 text-sm text-neutral-400">Key Fundamentals</div>
              <FundamentalsPanel data={fundamentals} />
            </div>
          </div>
        )}

        {tab === 'news' && (
          <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
            {news === null ? (
              <p className="text-sm text-neutral-500">Loading news...</p>
            ) : (
              <NewsList articles={news} />
            )}
          </div>
        )}

        {tab === 'earnings' && (
          <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
            {earnings === null ? (
              <p className="text-sm text-neutral-500">Loading earnings...</p>
            ) : (
              <EarningsTable earnings={earnings} />
            )}
          </div>
        )}

        {tab === 'filings' && (
          <div className="rounded border border-neutral-800 bg-neutral-900 p-5">
            {filings === null ? (
              <p className="text-sm text-neutral-500">Loading filings...</p>
            ) : (
              <FilingsList filings={filings} />
            )}
          </div>
        )}
      </div>
    </div>
  )
}
