import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../services/api'
import { PriceChart } from '../components/PriceChart'
import { FundamentalsPanel } from '../components/FundamentalsPanel'
import { NewsList } from '../components/NewsList'
import { EarningsTable } from '../components/EarningsTable'
import { FilingsList } from '../components/FilingsList'
import { ChangePill, Skeleton } from '../components/ui'
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

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <div className="text-xs text-ink-faint">{label}</div>
      <div className="nums mt-0.5 text-sm font-medium text-ink">{value}</div>
    </div>
  )
}

function Panel({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="rounded-xl border border-border bg-surface p-5">
      <div className="mb-4 text-sm font-medium text-ink-muted">{title}</div>
      {children}
    </div>
  )
}

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

  useEffect(() => {
    if (!ticker) return
    setLoading(true)
    setError(null)
    setTab('overview')
    setNews(null)
    setEarnings(null)
    setFilings(null)
    setChart([])

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
      <div className="mx-auto max-w-7xl px-5 py-8">
        <Skeleton className="h-4 w-32" />
        <Skeleton className="mt-3 h-7 w-64" />
        <Skeleton className="mt-6 h-32 w-full" />
        <Skeleton className="mt-6 h-72 w-full" />
      </div>
    )
  }

  if (error || !company || !quote) {
    return (
      <div className="mx-auto max-w-7xl px-5 py-8">
        <div className="rounded-xl border border-border bg-surface p-5 text-sm text-ink-muted">
          {error ?? 'Something went wrong.'}
        </div>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-7xl px-5 py-8">
      {/* Header */}
      <div className="flex flex-wrap items-center gap-2">
        <span className="rounded border border-border px-1.5 py-0.5 text-[10px] font-medium uppercase tracking-wide text-ink-faint">
          {company.exchange}
        </span>
        <span className="text-xs text-ink-faint">{company.sector}</span>
      </div>
      <div className="mt-1.5 flex items-baseline gap-3">
        <h1 className="text-2xl font-bold tracking-tight text-ink">{company.name}</h1>
        <span className="text-sm text-ink-faint">{company.ticker}</span>
      </div>

      {/* Price card */}
      <div className="mt-5 rounded-xl border border-border bg-surface p-5">
        <div className="flex flex-wrap items-center gap-3">
          <span className="nums text-4xl font-semibold tracking-tight text-ink">
            {money(quote.currentPrice)}
          </span>
          <ChangePill percent={quote.percentChange} />
          <span
            className="nums text-sm"
            style={{ color: quote.change >= 0 ? 'var(--color-up)' : 'var(--color-down)' }}
          >
            {quote.change >= 0 ? '+' : ''}
            {quote.change.toFixed(2)} today
          </span>
        </div>

        <div className="mt-5 grid grid-cols-2 gap-4 border-t border-border pt-4 sm:grid-cols-5">
          <Stat label="Open" value={money(quote.openPrice)} />
          <Stat label="Prev Close" value={money(quote.previousClose)} />
          <Stat label="Day High" value={money(quote.dayHigh)} />
          <Stat label="Day Low" value={money(quote.dayLow)} />
          <Stat
            label="52W Range"
            value={`${money(fundamentals?.week52Low)} – ${money(fundamentals?.week52High)}`}
          />
        </div>
      </div>

      {/* Tabs */}
      <div className="mt-6 flex gap-1 rounded-lg border border-border bg-surface p-1">
        {TABS.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className={`flex-1 rounded-md px-4 py-1.5 text-sm font-medium transition-colors sm:flex-none ${
              tab === t.id
                ? 'bg-surface-2 text-ink'
                : 'text-ink-muted hover:text-ink'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      <div className="mt-6">
        {tab === 'overview' && (
          <div className="space-y-6">
            <Panel title="Price Chart">
              <PriceChart data={chart} />
            </Panel>
            <Panel title="Key Fundamentals">
              <FundamentalsPanel data={fundamentals} />
            </Panel>
          </div>
        )}

        {tab === 'news' && (
          <Panel title="Recent News">
            {news === null ? <Skeleton className="h-40" /> : <NewsList articles={news} />}
          </Panel>
        )}

        {tab === 'earnings' && (
          <Panel title="Earnings History">
            {earnings === null ? <Skeleton className="h-40" /> : <EarningsTable earnings={earnings} />}
          </Panel>
        )}

        {tab === 'filings' && (
          <Panel title="SEC Filings">
            {filings === null ? <Skeleton className="h-40" /> : <FilingsList filings={filings} />}
          </Panel>
        )}
      </div>
    </div>
  )
}
