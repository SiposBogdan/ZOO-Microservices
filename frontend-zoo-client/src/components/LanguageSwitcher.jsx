import React from 'react';
import { useTranslation } from 'react-i18next';

export default function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const switchTo = (lng) => {
    i18n.changeLanguage(lng);
  };

  return (
    <div className="flex space-x-2">
      <button
        onClick={() => switchTo('ro')}
        className="px-3 py-1 border rounded hover:bg-gray-100"
      >
        Română
      </button>
      <button
        onClick={() => switchTo('en')}
        className="px-3 py-1 border rounded hover:bg-gray-100"
      >
        English
      </button>
      <button
        onClick={() => switchTo('fr')}
        className="px-3 py-1 border rounded hover:bg-gray-100"
      >
        Français
      </button>
    </div>
  );
}
