// src/api/animalApi.js
import axios from 'axios';

const animalApi = axios.create({
  baseURL:
    import.meta.env.VITE_ANIMAL_API_URL ||
    'http://localhost:8081/api/animal',  // ← singular endpoint
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT if present
animalApi.interceptors.request.use(cfg => {
  const t = localStorage.getItem('token');
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

export default animalApi;
