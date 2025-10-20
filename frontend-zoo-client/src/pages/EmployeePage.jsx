// src/pages/EmployeePage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import animalApi from '../api/animalApi';
import exemplarApi from '../api/exemplarApi';
import Header from '../components/common/Header';
import './EmployeePage.css';

export default function EmployeePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [tab, setTab] = useState('animal');
  const [animals, setAnimals] = useState([]);
  const [exemplars, setExemplars] = useState([]);
  const [error, setError] = useState(null);

  // Fetch animals once
  useEffect(() => {
    animalApi.get('')
      .then(resp => {
        const list = Array.isArray(resp.data)
          ? resp.data
          : Array.isArray(resp.data.animals)
            ? resp.data.animals
            : [];
        setAnimals(list);
      })
      .catch(() => setError(t('failedLoadAnimals')));
  }, [t]);

  // Fetch exemplars once
  useEffect(() => {
    exemplarApi.get('')
      .then(resp => {
        const list = Array.isArray(resp.data)
          ? resp.data
          : Array.isArray(resp.data.exemplars)
            ? resp.data.exemplars
            : [];
        setExemplars(list);
      })
      .catch(() => setError(t('failedLoadExemplars')));
  }, [t]);

  if (error) {
    return (
      <div className="employee-container">
        <Header />
        <p className="login-error">{t('error')}: {error}</p>
      </div>
    );
  }

  const currentList = tab === 'animal' ? animals : exemplars;

  return (
    <div className="employee-container">
      <Header />
      <h1 className="employee-title">{t('employeeDashboard')}</h1>

      {/* Tabs */}
      <div className="tab-group">
        <button
          className={`tab-button ${tab === 'animal' ? 'active' : 'inactive'}`}
          onClick={() => setTab('animal')}
        >
          {t('animals')}
        </button>
        <button
          className={`tab-button ${tab === 'exemplar' ? 'active' : 'inactive'}`}
          onClick={() => setTab('exemplar')}
        >
          {t('exemplars')}
        </button>
      </div>

      {/* Add New button */}
      <button
        className="add-new"
        onClick={() => navigate(tab === 'animal' ? '/animal/new' : '/exemplar/new')}
      >
        {t('addNew')} {t(tab === 'animal' ? 'animal' : 'exemplar')}
      </button>

      {/* Data Table */}
      <div className="employee-table-wrapper">
        <table className="employee-table">
          <thead>
            <tr>
              <th>{t('id')}</th>
              <th>{t('species')}</th>
              <th>{tab === 'animal' ? t('category') : t('location')}</th>
              <th>{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {currentList.length > 0 ? (
              currentList.map(item => (
                <tr key={item.id} className="hover:bg-gray-100">
                  <td>{item.id}</td>
                  <td>{item.name}</td>
                  <td>{tab === 'animal' ? item.category : item.location}</td>
                  <td className="actions">
                    <Link to={`/${tab}/${item.id}`} className="view">{t('view')}</Link>
                    <Link to={`/${tab}/${item.id}`} className="edit">{t('edit')}</Link>
                    <button
                      className="delete"
                      onClick={() => {
                        const api = tab === 'animal' ? animalApi : exemplarApi;
                        api.delete(`/${item.id}`)
                          .then(() => {
                            if (tab === 'animal') {
                              setAnimals(a => a.filter(x => x.id !== item.id));
                            } else {
                              setExemplars(e => e.filter(x => x.id !== item.id));
                            }
                          })
                          .catch(console.error);
                      }}
                    >
                      {t('delete')}
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="p-4 text-center text-gray-600">
                  {t('noFound', { tab: t(tab) })}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
