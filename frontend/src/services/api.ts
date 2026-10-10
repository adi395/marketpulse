import type {
  Company,
  Quote,
  Fundamentals,
  PriceBar,
  NewsArticle,
  Earnings,
  SecFiling,
  MarketOverview,
} from '../types/company'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api'

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`)
  if (!res.ok) {
    throw new Error(`API error ${res.status}: ${path}`)
  }
  return res.json()
}

export const api = {
  getCompany: (ticker: string) => get<Company>(`/companies/${ticker}`),
  getQuote: (ticker: string) => get<Quote>(`/companies/${ticker}/quote`),
  getFundamentals: (ticker: string) => get<Fundamentals>(`/companies/${ticker}/fundamentals`),
  getChart: (ticker: string, range = '6M') =>
    get<PriceBar[]>(`/companies/${ticker}/chart?range=${encodeURIComponent(range)}`),
  getNews: (ticker: string) => get<NewsArticle[]>(`/companies/${ticker}/news`),
  getEarnings: (ticker: string) => get<Earnings[]>(`/companies/${ticker}/earnings`),
  getFilings: (ticker: string) => get<SecFiling[]>(`/companies/${ticker}/filings`),
  getMarketOverview: () => get<MarketOverview>(`/market/overview`),
}
