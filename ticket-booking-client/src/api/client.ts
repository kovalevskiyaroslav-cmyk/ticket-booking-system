import axios, { AxiosInstance, AxiosError } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const apiClient: AxiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
});

apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
        if (error.response) {
            const status = error.response.status;
            const data = error.response.data as any;

            switch (status) {
                case 400:
                    console.error('Bad Request:', data);
                    break;
                case 404:
                    console.error('Not Found:', data);
                    break;
                case 409:
                    console.error('Conflict:', data);
                    break;
                case 500:
                    console.error('Server Error:', data);
                    break;
                default:
                    console.error(`HTTP ${status}:`, data);
            }
        } else if (error.request) {
            console.error('Network Error: Сервер недоступен');
        } else {
            console.error('Error:', error.message);
        }
        return Promise.reject(error);
    }
);

export default apiClient;