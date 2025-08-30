import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'

// Vite 환경변수에서 API 서버 주소 불러오기
const baseURL = import.meta.env.VITE_API_BASE_URL

export const http = axios.create({
  baseURL: baseURL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false, // CORS에서 쿠키 인증이 필요 없으므로 false
})

// 요청 인터셉터: 토큰 자동 추가
http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

export default http
