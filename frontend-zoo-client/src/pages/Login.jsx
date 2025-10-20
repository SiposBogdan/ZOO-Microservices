// src/pages/Login.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { decodeJwt } from '../utils/jwt';
import zooApi from '../api/zooApi';
import Header from '../components/common/Header';
import './Login.css';

export default function Login() {
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    try {
      const resp = await zooApi.post('/auth/login', { username, password });
      const token = resp.data.token;
      localStorage.setItem('token', token);

      const decoded = decodeJwt(token);
      const userRole = decoded.role || (Array.isArray(decoded.roles) ? decoded.roles[0] : null);

      if (userRole === 'ROLE_EMPLOYEE') {
        navigate('/employee', { replace: true });
      } else if (userRole === 'ROLE_MANAGER') {
        navigate('/manager', { replace: true });
      } else if (userRole === 'ROLE_ADMIN') {
        navigate('/admin', { replace: true });
      } else {
        navigate('/home', { replace: true });
      }
    } catch (err) {
      if (err.response && err.response.status === 401) {
        setError(t('invalidCredentials'));
      } else {
        setError(t('unexpectedError'));
      }
    }
  };

  return (
    <div className="login-container">
      <Header />

      <form onSubmit={handleSubmit} className="login-form">
        <h2 className="login-title">{t('loginTitle')}</h2>

        {error && <div className="login-error">{error}</div>}

        <input
          type="text"
          placeholder={t('usernamePlaceholder')}
          value={username}
          onChange={e => setUsername(e.target.value)}
          className="login-input"
          required
        />
        <input
          type="password"
          placeholder={t('passwordPlaceholder')}
          value={password}
          onChange={e => setPassword(e.target.value)}
          className="login-input"
          required
        />

        <button type="submit" className="login-button">
          {t('signIn')}
        </button>
      </form>
    </div>
  );
}
