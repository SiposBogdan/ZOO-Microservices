// src/pages/CreateUserPage.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import userApi from '../api/userApi';
import Header from '../components/common/Header';
import './CreateUserPage.css';

export default function CreateUserPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '',
    email: '',
    phone: '',
    userType: '',
    password: ''
  });
  const [error, setError] = useState('');

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    try {
      await userApi.post('', {
        username: form.username,
        email:    form.email,
        phone:    form.phone,
        userType: form.userType,
        password: form.password
      });
      navigate('/admin');
    } catch (err) {
      console.error(err);
      setError(t('createUserError'));
    }
  };

  return (
    <div className="cu-form-container">
      <Header />
      <h2 className="cu-form-title">{t('createUser')}</h2>
      {error && <div className="cu-form-error">{error}</div>}

      <form onSubmit={handleSubmit} className="cu-form">
        <div className="cu-form-group">
          <label htmlFor="username" className="cu-form-label">{t('username')}</label>
          <input
            id="username"
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder={t('usernamePlaceholder')}
            className="cu-form-input"
            required
          />
        </div>

        <div className="cu-form-group">
          <label htmlFor="email" className="cu-form-label">{t('email')}</label>
          <input
            id="email"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            placeholder={t('emailPlaceholder')}
            className="cu-form-input"
            required
          />
        </div>

        <div className="cu-form-group">
          <label htmlFor="phone" className="cu-form-label">{t('phone')}</label>
          <input
            id="phone"
            name="phone"
            value={form.phone}
            onChange={handleChange}
            placeholder={t('phonePlaceholder')}
            className="cu-form-input"
          />
        </div>

        <div className="cu-form-group">
          <label htmlFor="userType" className="cu-form-label">{t('role')}</label>
          <select
            id="userType"
            name="userType"
            value={form.userType}
            onChange={handleChange}
            className="cu-form-input"
            required
          >
            <option value="">{t('selectRolePlaceholder')}</option>
            <option value="VISITOR">{t('visitor')}</option>
            <option value="EMPLOYEE">{t('employee')}</option>
            <option value="MANAGER">{t('manager')}</option>
            <option value="ADMIN">{t('admin')}</option>
          </select>
        </div>

        <div className="cu-form-group">
          <label htmlFor="password" className="cu-form-label">{t('password')}</label>
          <input
            id="password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            placeholder={t('passwordPlaceholder')}
            className="cu-form-input"
            required
          />
        </div>

        <div className="cu-form-actions">
          <button type="submit" className="cu-button">{t('create')}</button>
          <button type="button" className="cu-cancel-button" onClick={() => navigate(-1)}>
            {t('cancel')}
          </button>
        </div>
      </form>
    </div>
  );
}
