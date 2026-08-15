import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'

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
    <header className="border-b border-neutral-800 bg-neutral-950">
      <div className="mx-auto flex max-w-7xl items-center gap-6 px-4 py-3">
        <Link to="/" className="text-sm font-semibold tracking-wide text-neutral-100">
          MARKETPULSE
        </Link>

        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleSearch}
          placeholder="Search ticker or company..."
          className="w-72 rounded border border-neutral-800 bg-neutral-900 px-3 py-1.5 text-sm text-neutral-200 placeholder-neutral-500 outline-none focus:border-neutral-600"
        />

        <nav className="flex gap-4 text-sm">
          <NavLink
            to="/"
            className={({ isActive }) =>
              isActive ? 'text-neutral-100' : 'text-neutral-500 hover:text-neutral-300'
            }
          >
            Dashboard
          </NavLink>
        </nav>
      </div>
    </header>
  )
}
