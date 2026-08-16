import type { Company, Quote, Fundamentals } from '../types/company'

const API_BASE = 'http://localhost:8080/api'

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
}
