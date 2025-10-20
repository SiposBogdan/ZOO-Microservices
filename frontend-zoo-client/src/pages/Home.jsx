// src/pages/Home.jsx
import React, { useEffect, useState } from 'react';
import animalApi from '../api/animalApi';
import exemplarApi from '../api/exemplarApi';
import { useTranslation } from 'react-i18next';
import Header from '../components/common/Header';
import './Home.css';

// Automatically import all images from the `src/images` directory (ESM-friendly)
const imageModules = import.meta.glob('../images/*.{png,jpg,jpeg,svg}', { eager: true });
const images = Object.fromEntries(
  Object.entries(imageModules).map(([fullPath, module]) => {
    const fileName = fullPath.split('/').pop();
    return [fileName, module.default];
  })
);

export default function Home() {
  const { t } = useTranslation();
  const [animals, setAnimals] = useState([]);
  const [exemplars, setExemplars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        setError('');
        const [aRes, eRes] = await Promise.all([
          animalApi.get(''),
          exemplarApi.get('')
        ]);
        setAnimals(aRes.data);
        setExemplars(eRes.data);
      } catch (err) {
        console.error(err);
        setError(t('failedLoadData'));
      } finally {
        setLoading(false);
      }
    })();
  }, [t]);

  if (loading) {
    return (
      <div className="home-container">
        <Header />
        <p className="home-title">{t('loadingData')}</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="home-container">
        <Header />
        <p className="home-title text-red-600">{error}</p>
      </div>
    );
  }

  const animalCols = animals.length > 0 ? Object.keys(animals[0]) : [];
  const exemplarCols = exemplars.length > 0 ? Object.keys(exemplars[0]) : [];

  const renderCell = (key, val) => {
    const lowerKey = key.toLowerCase();
    if (typeof val === 'string' && (lowerKey.includes('photo') || lowerKey.includes('image'))) {
      const fileName = val.split(/[\\/]/).pop();
      const src = images[fileName] || val;
      return <img src={src} alt={key} className="" />;
    }
    if (Array.isArray(val) && lowerKey.includes('image')) {
      return (
        <div className="flex flex-wrap gap-2">
          {val.map((item, idx) => {
            if (typeof item !== 'string') return null;
            const fileName = item.split(/[\\/]/).pop();
            const src = images[fileName] || item;
            return <img key={idx} src={src} alt={`${key}-${idx}`} className="" />;
          })}
        </div>
      );
    }
    if (val && typeof val === 'object') {
      return <pre>{JSON.stringify(val, null, 2)}</pre>;
    }
    return String(val);
  };

  return (
    <div className="home-container">
      <Header />
      <h1 className="home-title">{t('welcome')}</h1>

      <section>
        <h2 className="section-title">{t('animals')}</h2>
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                {animalCols.map(col => (
                  <th key={col}>{t(col)}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {animals.map((row, i) => (
                <tr key={i}>
                  {animalCols.map(col => (
                    <td key={col}>{renderCell(col, row[col])}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section>
        <h2 className="section-title">{t('exemplars')}</h2>
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                {exemplarCols.map(col => (
                  <th key={col}>{t(col)}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {exemplars.map((row, i) => (
                <tr key={i}>
                  {exemplarCols.map(col => (
                    <td key={col}>{renderCell(col, row[col])}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
