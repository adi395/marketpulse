export interface Company {
  ticker: string
  name: string
  sector: string
  exchange: string
}

export interface Quote {
  ticker: string
  currentPrice: number
  change: number
  percentChange: number
  dayHigh: number
  dayLow: number
  openPrice: number
  previousClose: number
  timestamp: number
}

export interface Fundamentals {
  peTTM: number | null
  peNormalized: number | null
  priceToSalesTTM: number | null
  priceToBook: number | null
  grossMarginTTM: number | null
  operatingMarginTTM: number | null
  netMarginTTM: number | null
  epsTTM: number | null
  epsGrowthYoY: number | null
  revenueGrowthYoY: number | null
  debtToEquity: number | null
  week52High: number | null
  week52Low: number | null
}

export interface PriceBar {
  date: string
  open: number
  high: number
  low: number
  close: number
  volume: number
}

export interface NewsArticle {
  headline: string
  source: string
  summary: string
  url: string
  imageUrl: string
  publishedAt: number
}

export interface Earnings {
  period: string
  year: number
  quarter: number
  epsActual: number | null
  epsEstimate: number | null
  surprisePercent: number | null
}

export interface SecFiling {
  formType: string
  filedDate: string
  filingUrl: string
}
