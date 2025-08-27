import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? 'http://localhost:8081',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false,
})
