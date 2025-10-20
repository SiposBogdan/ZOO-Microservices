// src/pages/EditUserPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import userApi from '../api/userApi';
import Header from '../components/common/Header';
import './EditUserPage.css';

export default function EditUserPage() {
  const { t } = useTranslation();
  const { id } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: '',
    email: '',
    phone: '',
    userType: '',
    password: ''    // optional: leave blank to keep existing
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Fetch existing user
  useEffect(() => {
    async function fetchUser() {
      try {
        setLoading(true);
        setError('');
        const resp = await userApi.get(`/${id}`);
        const u = resp.data;
        setForm({
          username: u.username || '',
          email:    u.email    || '',
          phone:    u.phone    || '',
          userType: u.userType || '',
          password: ''  // always start blank
        });
      } catch (err) {
        console.error(err);
        setError(t('userLoadError'));
      } finally {
        setLoading(false);
      }
    }
    fetchUser();
  }, [id, t]);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      setError('');
      const dto = {
        username: form.username,
        email:    form.email,
        phone:    form.phone,
        userType: form.userType,
        password: form.password  // backend skips if blank
      };
      await userApi.put(`/${id}`, dto);
      navigate('/admin');
    } catch (err) {
      console.error(err);
      const serverMsg = err.response?.data?.message || '';
      if (serverMsg.includes('Duplicate entry')) {
        setError(t('duplicateEmailError'));
      } else {
        setError(t('updateUserError'));
      }
    }
  };

  if (loading) return <div className="eu-loading">{t('loadingUser')}</div>;

  return (
    <div className="eu-form-container">
      <Header />
      <h2 className="eu-form-title">{t('editUser', { id })}</h2>
      {error && <div className="eu-form-error">{error}</div>}

      <form onSubmit={handleSubmit} className="eu-form">
        <div className="eu-form-group">
          <label htmlFor="username" className="eu-form-label">{t('username')}</label>
          <input
            id="username"
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder={t('usernamePlaceholder')}
            className="eu-form-input"
            required
          />
        </div>

        <div className="eu-form-group">
          <label htmlFor="email" className="eu-form-label">{t('email')}</label>
          <input
            id="email"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            placeholder={t('emailPlaceholder')}
            className="eu-form-input"
            required
          />
        </div>

        <div className="eu-form-group">
          <label htmlFor="phone" className="eu-form-label">{t('phone')}</label>
          <input
            id="phone"
            name="phone"
            value={form.phone}
            onChange={handleChange}
            placeholder={t('phonePlaceholder')}
            className="eu-form-input"
          />
        </div>

        <div className="eu-form-group">
          <label htmlFor="userType" className="eu-form-label">{t('role')}</label>
          <select
            id="userType"
            name="userType"
            value={form.userType}
            onChange={handleChange}
            className="eu-form-input"
            required
          >
            <option value="">{t('selectRolePlaceholder')}</option>
            <option value="VISITOR">{t('visitor')}</option>
            <option value="EMPLOYEE">{t('employee')}</option>
            <option value="MANAGER">{t('manager')}</option>
            <option value="ADMIN">{t('admin')}</option>
          </select>
        </div>

        <div className="eu-form-group">
          <label htmlFor="password" className="eu-form-label">
            {t('newPassword')} <span className="eu-form-hint">{t('leaveBlank')}</span>
          </label>
          <input
            id="password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            placeholder={t('passwordPlaceholder')}
            className="eu-form-input"
          />
        </div>

        <div className="eu-form-actions">
          <button type="submit" className="eu-button-save">{t('save')}</button>
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="eu-button-cancel"
          >
            {t('cancel')}
          </button>
        </div>
      </form>
    </div>
  );
}
