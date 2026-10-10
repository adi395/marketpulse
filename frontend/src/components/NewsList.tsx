import type { NewsArticle } from '../types/company'
import { fromUnix } from '../utils/format'

export function NewsList({ articles }: { articles: NewsArticle[] }) {
  if (articles.length === 0) {
    return <p className="text-sm text-ink-faint">No recent news.</p>
  }

  return (
    <ul className="divide-y divide-border">
      {articles.map((a, i) => (
        <li key={`${a.url}-${i}`} className="py-4 first:pt-0 last:pb-0">
          <a href={a.url} target="_blank" rel="noopener noreferrer" className="group flex gap-4">
            {a.imageUrl && (
              <img
                src={a.imageUrl}
                alt=""
                className="hidden h-16 w-24 shrink-0 rounded-lg border border-border object-cover sm:block"
                onError={(e) => (e.currentTarget.style.display = 'none')}
              />
            )}
            <div className="min-w-0">
              <div className="text-sm font-medium text-ink transition-colors group-hover:text-accent-soft">
                {a.headline}
              </div>
              {a.summary && <p className="mt-1 line-clamp-2 text-xs text-ink-muted">{a.summary}</p>}
              <div className="mt-1.5 text-xs text-ink-faint">
                {a.source} · {fromUnix(a.publishedAt)}
              </div>
            </div>
          </a>
        </li>
      ))}
    </ul>
  )
}
