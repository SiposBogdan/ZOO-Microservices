import axios from 'axios';

const userApi = axios.create({
  baseURL: 'http://localhost:8083/api/user', // 🧠 8083 should be your backend port
  headers: { 'Content-Type': 'application/json' },
});

userApi.interceptors.request.use(cfg => {
  const t = localStorage.getItem('token');
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

export default userApi;
