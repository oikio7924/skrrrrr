import axios, { type InternalAxiosRequestConfig } from "axios";

// Axios 인스턴스 생성
// Axios : front인 Vue와 Back인 Spring Boot를 연결하는 역할
//         Vue에 코드 작성하여 vue에서 요청을 보내면 이 인스턴스가 요청을 받아서 처리한다.
const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem("accessToken"); // 토큰 키 확인!
    if (token) {
      // ✅ 개별 속성으로 추가해야 함
      config.headers = config.headers || {};
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default apiClient;
