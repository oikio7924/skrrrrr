import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'

export const http = axios.create({
  // 서버 주소를 올바른 8081 포트로 직접 수정합니다.
  baseURL: 'http://localhost:8081',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false,
})

// 요청 인터셉터 추가
http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('access_token')

  // 모든 요청에 토큰을 붙이도록 수정
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }

  return config
})

export default http
