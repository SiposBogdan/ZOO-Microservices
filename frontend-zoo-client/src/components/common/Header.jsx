// src/components/common/Header.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import LanguageSwitcher from '../LanguageSwitcher.jsx';

export default function Header() {
  return (
    <header className="flex items-center justify-between p-4 bg-gray-100">
      <Link to="/">
        <h2 className="text-xl font-bold">Zoo App</h2>
      </Link>
      {/* Language buttons always visible */}
      <LanguageSwitcher />
    </header>
  );
}
