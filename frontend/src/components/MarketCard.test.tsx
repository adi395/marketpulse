import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { MarketCard } from './MarketCard'
import type { MarketMover } from '../types/company'

function renderCard(overrides: Partial<MarketMover> = {}) {
  const mover: MarketMover = {
    ticker: 'AAPL',
    name: 'Apple Inc.',
    sector: 'Technology',
    exchange: 'NASDAQ',
    currentPrice: 336.64,
    change: -3.78,
    percentChange: -1.11,
    ...overrides,
  }
  return render(
    <MemoryRouter>
      <MarketCard mover={mover} />
    </MemoryRouter>,
  )
}

describe('MarketCard', () => {
  it('renders ticker, name and formatted price', () => {
    renderCard()
    expect(screen.getByText('AAPL')).toBeInTheDocument()
    expect(screen.getByText('Apple Inc.')).toBeInTheDocument()
    expect(screen.getByText('$336.64')).toBeInTheDocument()
  })

  it('links through to the company page', () => {
    renderCard()
    expect(screen.getByRole('link')).toHaveAttribute('href', '/company/AAPL')
  })

  it('shows a signed percent change for gainers', () => {
    renderCard({ percentChange: 3.29 })
    expect(screen.getByText('+3.29%')).toBeInTheDocument()
  })

  it('falls back to N/A when the quote is unavailable', () => {
    renderCard({ currentPrice: null, percentChange: null })
    expect(screen.getAllByText('N/A').length).toBeGreaterThan(0)
  })
})
