// src/i18n.js
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// ⚡️ Use import.meta.glob with eager:true
const modules = import.meta.glob('./locales/*/translation.json', { eager: true });

const resources = Object.entries(modules).reduce((res, [path, mod]) => {
  // path === './locales/en/translation.json'
  const lang = path.split('/')[2];      // 'en' | 'fr' | 'es'
  res[lang] = { translation: mod.default };
  return res;
}, {});

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'en',            // default language
    fallbackLng: 'en',    // fallback if key is missing
    interpolation: { escapeValue: false }
  });

export default i18n;
