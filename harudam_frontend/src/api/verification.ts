import { http } from './http'

export type SendCodeReq = { childId: number; phone: string }
export type VerifyReq = { phone: string; code: string }
export type SimpleRes = { success: boolean; message: string }

export async function sendVerificationCode(payload: SendCodeReq) {
  const { data } = await http.post<SimpleRes>('/api/verification/send-code', payload)
  return data
}

export async function verifyCode(payload: VerifyReq) {
  const { data } = await http.post<SimpleRes>('/api/verification/verify', payload)
  return data
}
