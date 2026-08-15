import { useParams } from 'react-router-dom'

export function CompanyPage() {
  const { ticker } = useParams()
  return (
    <div className="mx-auto max-w-7xl px-4 py-6">
      <h1 className="text-lg text-neutral-200">{ticker}</h1>
      <p className="mt-2 text-sm text-neutral-500">Company research page coming next.</p>
    </div>
  )
}
