import axios from 'axios';

const exemplarApi = axios.create({
  baseURL: 'http://localhost:8084/api/exemplar',
  headers: {
    'Content-Type': 'application/json'
  }
});

exemplarApi.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default exemplarApi; // ✅ this line is essential
