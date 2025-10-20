// src/pages/ManagerPage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Bar, Pie } from 'react-chartjs-2';
import {
  Chart,
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
} from 'chart.js';
import { useTranslation } from 'react-i18next';
import animalApi from '../api/animalApi';
import exemplarApi from '../api/exemplarApi';
import Header from '../components/common/Header';
import './ManagerPage.css';

Chart.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

export default function ManagerPage() {
  const { t } = useTranslation();
  const [stats, setStats] = useState(null);
  const [animals, setAnimals] = useState([]);
  const [exemplars, setExemplars] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Load stats
  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = localStorage.getItem('token');
        const resp = await fetch('http://localhost:8081/api/animal/stats', {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (!resp.ok) throw new Error('Failed to fetch stats');
        const data = await resp.json();
        setStats(data);
      } catch (err) {
        console.error('Stats error:', err);
        setError(t('statsLoadError'));
      }
    };
    fetchStats();
  }, [t]);

  // Load animals & exemplars
  useEffect(() => {
    const fetchAnimals = async () => {
      try {
        const resp = await animalApi.get('');
        setAnimals(resp.data);
      } catch (err) {
        console.error('Animal fetch error:', err);
        setError(t('animalsLoadError'));
      }
    };
    const fetchExemplars = async () => {
      try {
        const resp = await exemplarApi.get('');
        setExemplars(resp.data);
      } catch (err) {
        console.error('Exemplar fetch error:', err);
        setError(t('exemplarsLoadError'));
      }
    };
    fetchAnimals();
    fetchExemplars();
  }, [t]);

  const handleDeleteAnimal = async id => {
    if (!window.confirm(t('confirmDeleteAnimal'))) return;
    try {
      await animalApi.delete(`/${id}`);
      setAnimals(a => a.filter(x => x.id !== id));
    } catch (err) {
      console.error('Delete error:', err);
      setError(t('deleteAnimalError'));
    }
  };

  const handleDeleteExemplar = async id => {
    if (!window.confirm(t('confirmDeleteExemplar'))) return;
    try {
      await exemplarApi.delete(`/${id}`);
      setExemplars(e => e.filter(x => x.id !== id));
    } catch (err) {
      console.error('Exemplar delete error:', err);
      setError(t('deleteExemplarError'));
    }
  };

  const handleExport = () => {
    window.open('http://localhost:8081/api/animal/stats/export', '_blank');
  };

  if (error) return <div className="text-red-600">{error}</div>;
  if (!stats) return <div>{t('loadingStats')}</div>;

  const barData = {
    labels: Object.keys(stats.countPerCategory || {}),
    datasets: [
      {
        label: t('animalsPerCategory'),
        data: Object.values(stats.countPerCategory || {}),
        backgroundColor: ['#3182ce', '#63b3ed', '#90cdf4', '#bee3f8'],
      },
    ],
  };

  const pieData = {
    labels: Object.keys(stats.dietDistribution || {}),
    datasets: [
      {
        label: t('dietTypeDistribution'),
        data: Object.values(stats.dietDistribution || {}),
        backgroundColor: ['#f56565', '#ed8936', '#ecc94b', '#48bb78'],
      },
    ],
  };

  return (
    <div className="manager-container">
      <Header />
      <h1 className="manager-title">{t('managerDashboard')}</h1>

      {/* Charts */}
      <div className="chart-grid">
        <div className="chart-section">
          <h2 className="section-title">{t('animalsByCategory')}</h2>
          <Bar data={barData} />
        </div>
        <div className="chart-section">
          <h2 className="section-title">{t('dietTypeBreakdown')}</h2>
          <Pie data={pieData} />
        </div>
      </div>

      <div className="averages">
        <h2 className="section-title">{t('averages')}</h2>
        <p>{t('averageAge')}: {stats.avgAge.toFixed(2)}</p>
        <p>{t('averageWeight')}: {stats.avgWeight.toFixed(2)}</p>
        <button onClick={handleExport} className="export-button">
          {t('exportToWord')}
        </button>
      </div>

      {/* Animal Management */}
      <div className="management-section">
        <div className="management-header">
          <h2 className="section-title">{t('animalManagement')}</h2>
          <button onClick={() => navigate('/animal/new')} className="add-button">
            {t('addNewAnimal')}
          </button>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('id')}</th>
              <th>{t('name')}</th>
              <th>{t('category')}</th>
              <th>{t('diet')}</th>
              <th>{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {animals.map(animal => (
              <tr key={animal.id}>
                <td>{animal.id}</td>
                <td>{animal.name}</td>
                <td>{animal.category}</td>
                <td>{animal.dietType}</td>
                <td className="actions">
                  <button onClick={() => navigate(`/animal/${animal.id}`)} className="action-button edit-button">
                    {t('edit')}
                  </button>
                  <button onClick={() => handleDeleteAnimal(animal.id)} className="action-button delete-button">
                    {t('delete')}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Exemplar Management */}
      <div>
        <div className="management-header">
          <h2 className="section-title">{t('exemplarManagement')}</h2>
          <button onClick={() => navigate('/exemplar/new')} className="add-button">
            {t('addNewExemplar')}
          </button>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('id')}</th>
              <th>{t('name')}</th>
              <th>{t('animalId')}</th>
              <th>{t('location')}</th>
              <th>{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {exemplars.map(ex => (
              <tr key={ex.id}>
                <td>{ex.id}</td>
                <td>{ex.name}</td>
                <td>{ex.animalId}</td>
                <td>{ex.location}</td>
                <td className="actions">
                  <button onClick={() => navigate(`/exemplar/${ex.id}`)} className="action-button edit-button">
                    {t('edit')}
                  </button>
                  <button onClick={() => handleDeleteExemplar(ex.id)} className="action-button delete-button">
                    {t('delete')}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
