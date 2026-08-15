import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Navbar } from './components/Navbar'
import { Dashboard } from './pages/Dashboard'
import { CompanyPage } from './pages/CompanyPage'

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-neutral-950">
        <Navbar />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/company/:ticker" element={<CompanyPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App
