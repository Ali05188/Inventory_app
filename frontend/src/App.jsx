import { useState, useEffect } from 'react'
import axios from 'axios'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
function Dashboard() {
  return <div className="p-4"><h1 className="text-2xl font-bold">Inventory Dashboard</h1></div>
}
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Dashboard />} />
      </Routes>
    </BrowserRouter>
  )
}
export default App
