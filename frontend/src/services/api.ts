const API_BASE = 'http://localhost:8080/api'

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`)
  if (!res.ok) {
    throw new Error(`API error ${res.status}: ${path}`)
  }
  return res.json()
}

export const api = {
  getCompany: (ticker: string) => get(`/companies/${ticker}`),
  getQuote: (ticker: string) => get(`/companies/${ticker}/quote`),
  getChart: (ticker: string) => get(`/companies/${ticker}/chart`),
  getFundamentals: (ticker: string) => get(`/companies/${ticker}/fundamentals`),
  getNews: (ticker: string) => get(`/companies/${ticker}/news`),
  getFilings: (ticker: string) => get(`/companies/${ticker}/filings`),
  getEarnings: (ticker: string) => get(`/companies/${ticker}/earnings`),
}
