import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'

function Logo() {
  return (
    <svg width="22" height="22" viewBox="0 0 64 64" fill="none" aria-hidden="true">
      <path
        d="M10 40 L22 40 L28 24 L36 48 L42 34 L47 34 L54 22"
        fill="none"
        stroke="var(--color-accent)"
        strokeWidth="5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="54" cy="22" r="5" fill="var(--color-up)" />
    </svg>
  )
}

export function Navbar() {
  const [query, setQuery] = useState('')
  const navigate = useNavigate()

  function handleSearch(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Enter' && query.trim()) {
      navigate(`/company/${query.trim().toUpperCase()}`)
      setQuery('')
    }
  }

  return (
    <header className="sticky top-0 z-20 border-b border-border bg-base/80 backdrop-blur-md">
      <div className="mx-auto flex max-w-7xl items-center gap-5 px-5 py-3">
        <Link to="/" className="flex items-center gap-2">
          <Logo />
          <span className="text-[15px] font-bold tracking-tight text-ink">
            Market<span className="text-accent">Pulse</span>
          </span>
        </Link>

        <div className="relative ml-2 flex-1 sm:max-w-sm">
          <svg
            className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-ink-faint"
            width="15"
            height="15"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="11" cy="11" r="7" />
            <path d="m21 21-4.3-4.3" />
          </svg>
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleSearch}
            placeholder="Search ticker or company…"
            className="w-full rounded-lg border border-border bg-surface py-2 pl-9 pr-3 text-sm text-ink placeholder-ink-faint outline-none transition-colors focus:border-accent/70 focus:bg-surface-2"
          />
        </div>

        <nav className="flex items-center gap-1 text-sm">
          <NavLink
            to="/"
            className={({ isActive }) =>
              `rounded-md px-3 py-1.5 transition-colors ${
                isActive ? 'bg-surface-2 text-ink' : 'text-ink-muted hover:text-ink'
              }`
            }
          >
            Dashboard
          </NavLink>
        </nav>
      </div>
    </header>
  )
}
