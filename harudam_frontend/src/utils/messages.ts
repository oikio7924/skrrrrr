// src/utils/messages.ts

export const successMessages: Record<string, string> = {
  LOGIN_SUCCESS: "로그인 성공",
  CODE_SENT: "인증번호가 발송되었습니다.",
  VERIFICATION_SUCCESS: "인증이 완료되었습니다.",
}

export const errorMessages: Record<string, string> = {
  LOGIN_FAILED: "로그인에 실패했습니다.",
  VERIFICATION_FAILED: "인증번호가 올바르지 않습니다.",
  NETWORK_ERROR: "네트워크 오류가 발생했습니다.",
}
