// src/api/verification.ts
import http from '@/api/http'

/** 공통 타입 */
export type VerifyReq = { phone: string; code: string }
export type ApiResponse<T = unknown> = {
  success: boolean
  message?: string
  data?: T
}

/** 자녀 */
export type SendChildCodeReq = { childId: number; phone: string }
export const sendChildCode = (p: SendChildCodeReq) =>
  http.post<ApiResponse>('/api/verification/send-child', p).then(r => r.data)

export const verifyChildCode = (p: VerifyReq) =>
  http.post<ApiResponse>('/api/verification/verify-child', p).then(r => r.data)

/** 부모 */
export type SendParentCodeReq = { phone: string }
export const sendParentCode = (p: SendParentCodeReq) =>
  http.post<ApiResponse>('/api/verification/send-parent', p).then(r => r.data)

export const verifyParentCode = (p: VerifyReq) =>
  http.post<ApiResponse>('/api/verification/verify-parent', p).then(r => r.data)
