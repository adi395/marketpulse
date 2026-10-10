const KEY = 'marketpulse_user_id'

/**
 * A stable per-browser identifier used to scope the watchlist. Stored in
 * localStorage. This identifies a client but does not authenticate it — it is
 * tenant scoping, not auth.
 */
export function getUserId(): string {
  try {
    let id = localStorage.getItem(KEY)
    if (!id) {
      id = crypto.randomUUID()
      localStorage.setItem(KEY, id)
    }
    return id
  } catch {
    // Private mode / storage blocked: fall back to a volatile id for this load.
    return 'anonymous'
  }
}
