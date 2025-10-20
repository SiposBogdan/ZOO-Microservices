// src/api/zooApi.js
import axios from 'axios';

const zooApi = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8083/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT on every request if present
zooApi.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default zooApi;
