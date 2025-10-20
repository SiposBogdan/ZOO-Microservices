// src/pages/AnimalForm.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import animalApi from '../api/animalApi';
import { decodeJwt } from '../utils/jwt';
import Header from '../components/common/Header';
import './AnimalForm.css';

export default function AnimalForm({ isEdit }) {
  const { t } = useTranslation();
  const { id } = useParams();
  const nav = useNavigate();

  const [form, setForm] = useState({
    name: '',
    category: '',
    dietType: '',
    habitat: '',
    averageWeight: '',
    averageAge: ''
  });
  const [error, setError] = useState('');

  // Load existing data when editing
  useEffect(() => {
    if (!isEdit) return;
    animalApi.get(`/${id}`)
      .then(({ data: d }) => {
        setForm({
          name:           d.name           || '',
          category:       d.category       || '',
          dietType:       d.dietType       || '',
          habitat:        d.habitat        || '',
          averageWeight:  d.averageWeight  != null ? String(d.averageWeight) : '',
          averageAge:     d.averageAge     != null ? String(d.averageAge)    : ''
        });
      })
      .catch(() => setError(t('failedLoadAnimal')));
  }, [id, isEdit, t]);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    const payload = {
      name:          form.name.trim(),
      category:      form.category.trim(),
      dietType:      form.dietType.trim(),
      habitat:       form.habitat.trim(),
      averageWeight: Number(form.averageWeight),
      averageAge:    Number(form.averageAge)
    };

    try {
      if (isEdit) await animalApi.put(`/${id}`, payload);
      else         await animalApi.post('', payload);

      // Redirect based on role
      const token = localStorage.getItem('token');
      let dest = '/home';
      if (token) {
        const { role, roles } = decodeJwt(token);
        const r = role || (Array.isArray(roles) ? roles[0] : null);
        dest = r === 'ROLE_EMPLOYEE' ? '/employee'
             : r === 'ROLE_MANAGER'  ? '/manager'
             : r === 'ROLE_ADMIN'    ? '/admin'
             : '/home';
      }
      nav(dest, { replace: true });
    } catch {
      setError(t('saveFailed'));
    }
  };

  return (
    <div className="an-form-container">
      <Header />
      <h1 className="an-form-title">
        {isEdit ? t('editAnimal') : t('addAnimal')}
      </h1>
      {error && <div className="an-form-error">{error}</div>}

      <form onSubmit={handleSubmit} className="an-form">
        {[
          ['name',          'specie',        'text'],
          ['category',      'category',      'text'],
          ['dietType',      'dietType',      'text'],
          ['habitat',       'habitat',       'text'],
          ['averageWeight', 'averageWeight', 'number'],
          ['averageAge',    'averageAge',    'number']
        ].map(([key, labelKey, type]) => (
          <div key={key} className="an-form-group">
            <label htmlFor={key} className="an-form-label">
              {t(labelKey)}
            </label>
            <input
              id={key}
              name={key}
              type={type}
              value={form[key]}
              onChange={handleChange}
              placeholder={t(`${labelKey}Placeholder`)}
              className="an-form-input"
              required
            />
          </div>
        ))}

        <button type="submit" className="an-form-button">
          {isEdit ? t('update') : t('create')}
        </button>
      </form>
    </div>
  );
}
