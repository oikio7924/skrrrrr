import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false,
})

export default http;
