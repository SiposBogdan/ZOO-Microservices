// src/pages/MainPage.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Header from '../components/common/Header';
import './MainPage.css';

export default function MainPage() {
  const { t } = useTranslation();

  return (
    <div className="main-container">
      <Header />
      <main className="main-content">
        <h1 className="main-title">{t('welcome')}</h1>

        <div className="button-group">
          <Link to="/home" className="button button-home">
            {t('goHome')}
          </Link>
          <Link to="/login" className="button button-login">
            {t('login')}
          </Link>
        </div>
      </main>
    </div>
  );
}
