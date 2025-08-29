// src/types/kakao.d.ts

// 카카오 SDK에서 실제로 사용하는 메서드만 "최소" 타입으로 선언
export interface KakaoAuthResponse {
  accessToken: string;
  expires_in: number;
  refresh_token: string;
  refresh_token_expires_in: number;
  scope?: string;
  token_type: string;
}

export interface KakaoSDK {
  init(key: string): void;
  isInitialized(): boolean;
  Auth: {
    login(opts: {
      scope?: string;
      success(res: KakaoAuthResponse): void;
      fail(err: unknown): void;
    }): void;
  };
  API: {
    request(opts: {
      url: string;
      success(res: unknown): void;
      fail(err: unknown): void;
    }): void;
  };
}

// 전역 window에 Kakao가 있을 수도 / 없을 수도 있으므로 optional 로 선언
declare global {
  interface Window {
    Kakao?: KakaoSDK;
  }
}

export {}; // 모듈로 인식시키기
