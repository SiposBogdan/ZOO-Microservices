import axios from 'axios';

const authApi = axios.create({
  baseURL: import.meta.env.VITE_API_AUTH_URL,
  headers: { 'Content-Type': 'application/json' },
});

authApi.interceptors.request.use(cfg => {
  const t = localStorage.getItem('token');
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

export default authApi;
