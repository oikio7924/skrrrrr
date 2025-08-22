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

// 모든 API 요청을 보내기 전에 이 코드가 먼저 실행된다.
apiClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: any) => {
        return Promise.reject(error);
    }
);

export default apiClient;