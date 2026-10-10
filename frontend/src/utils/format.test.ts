import { describe, it, expect } from 'vitest'
import { num, ratio, pct, money, fromUnix, shortDate } from './format'

describe('format helpers', () => {
  it('formats numbers and falls back to N/A for nullish/NaN', () => {
    expect(num(3.14159)).toBe('3.14')
    expect(num(3.14159, 1)).toBe('3.1')
    expect(num(null)).toBe('N/A')
    expect(num(undefined)).toBe('N/A')
    expect(num(NaN)).toBe('N/A')
  })

  it('formats ratios with an x suffix', () => {
    expect(ratio(37.8353)).toBe('37.84x')
    expect(ratio(null)).toBe('N/A')
  })

  it('formats percentages', () => {
    expect(pct(48.65)).toBe('48.65%')
    expect(pct(null)).toBe('N/A')
  })

  it('formats money with a dollar sign', () => {
    expect(money(336.6)).toBe('$336.60')
    expect(money(null)).toBe('N/A')
  })

  it('converts a unix-seconds timestamp to a date string', () => {
    // 2021-01-01T12:00:00Z — noon UTC stays on Jan 1, 2021 in every timezone.
    const formatted = fromUnix(1609502400)
    expect(formatted).toContain('2021')
    expect(formatted).toContain('Jan')
  })

  it('normalizes backend datetime strings and passes through unparseable input', () => {
    expect(shortDate('2026-07-31 00:00:00')).toContain('2026')
    expect(shortDate('not-a-date')).toBe('not-a-date')
  })
})
