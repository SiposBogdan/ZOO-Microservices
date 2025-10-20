// src/pages/ExemplarForm.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams }      from 'react-router-dom';
import { useTranslation }              from 'react-i18next';
import exemplarApi                    from '../api/exemplarApi';
import { decodeJwt }                  from '../utils/jwt';
import Header                         from '../components/common/Header';
import './ExemplarForm.css';  // or .module.css + import styles

export default function ExemplarForm({ isEdit }) {
  const { t }   = useTranslation();
  const { id }  = useParams();
  const nav     = useNavigate();

  const [form, setForm]   = useState({ animalId:'', name:'', images:'', location:'', age:'', weight:'', notes:'' });
  const [error, setError] = useState('');

  // Load for edit
  useEffect(() => {
    if (!isEdit) return;
    exemplarApi.get(`/${id}`)
      .then(({ data: d }) => {
        setForm({
          animalId: d.animalId?.toString() || '',
          name:     d.name    || '',
          images:   Array.isArray(d.images) ? d.images.join(',') : '',
          location: d.location || '',
          age:      d.age?.toString()    || '',
          weight:   d.weight?.toString() || '',
          notes:    d.notes   || ''
        });
      })
      .catch(() => setError(t('failedLoadExemplar')));
  }, [id, isEdit, t]);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    const payload = {
      animalId: Number(form.animalId),
      name:     form.name.trim(),
      images:   form.images.split(',').map(u=>u.trim()).filter(u=>u),
      location: form.location.trim(),
      age:      Number(form.age),
      weight:   Number(form.weight),
      notes:    form.notes.trim()
    };
    try {
      if (isEdit) await exemplarApi.put(`/${id}`, payload);
      else         await exemplarApi.post('', payload);

      // redirect based on role…
      const token = localStorage.getItem('token');
      let dest = '/home';
      if (token) {
        const { role, roles } = decodeJwt(token);
        const r = role || (Array.isArray(roles) ? roles[0] : null);
        dest = r==='ROLE_EMPLOYEE'?'/employee'
             : r==='ROLE_MANAGER' ?'/manager'
             : r==='ROLE_ADMIN'?   '/admin'
             : '/home';
      }
      nav(dest, { replace: true });

    } catch {
      setError(t('saveFailed'));
    }
  };

  return (
    <div className="ex-form-container">
      <Header />
      <h1 className="ex-form-title">
        {isEdit ? t('editExemplar') : t('addExemplar')}
      </h1>
      {error && <div className="ex-form-error">{error}</div>}
      <form onSubmit={handleSubmit} className="ex-form">
        {[
          ['animalId','animalId','number'],
          ['name','name','text'],
          ['images','images','text'],
          ['location','location','text'],
          ['age','age','number'],
          ['weight','weight','number'],
          ['notes','notes','text']
        ].map(([key,label,type]) => (
          <div key={key} className="ex-form-group">
            <label className="ex-form-label">{t(label)}</label>
            <input
              name={key}
              type={type}
              value={form[key]}
              onChange={handleChange}
              placeholder={t(`${label}Placeholder`)}
              className="ex-form-input"
              required={key !== 'notes'}
            />
          </div>
        ))}
        <button type="submit" className="ex-form-button">
          {isEdit ? t('update') : t('create')}
        </button>
      </form>
    </div>
  );
}
