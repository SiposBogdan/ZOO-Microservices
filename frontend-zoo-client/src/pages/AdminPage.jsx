// src/components/AdminPage.jsx
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
import userApi from '../api/userApi';
import animalApi from '../api/animalApi';
import exemplarApi from '../api/exemplarApi';
import Header from '../components/common/Header';
import './AdminPage.css';

Chart.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

export default function AdminPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  // Users
  const [users, setUsers] = useState([]);
  const [filter, setFilter] = useState('');
  const [userError, setUserError] = useState('');
  const [userLoading, setUserLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      setUserLoading(true);
      setUserError('');
      const resp = await userApi.get(filter ? `?type=${filter}` : '');
      const data = resp.data;
      if (Array.isArray(data)) setUsers(data);
      else if (Array.isArray(data.users)) setUsers(data.users);
      else if (Array.isArray(data.content)) setUsers(data.content);
      else throw new Error('Unexpected data format');
    } catch (err) {
      console.error(err);
      setUsers([]);
      setUserError(t('usersLoadError'));
    } finally {
      setUserLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [filter, t]);

  const handleDeleteUser = async (id) => {
    if (!window.confirm(t('confirmDeleteUser'))) return;
    try {
      await userApi.delete(`/${id}`);
      fetchUsers();
    } catch (err) {
      console.error(err);
      setUserError(t('deleteUserError'));
    }
  };

  // Export helpers
  const downloadBlob = (blob, filename) => {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  };

  const usersToXml = (list) => {
    let xml = '<?xml version="1.0" encoding="UTF-8"?><users>';
    list.forEach(u => {
      xml += '<user>';
      xml += `<id>${u.id}</id>`;
      xml += `<username><![CDATA[${u.username}]]></username>`;
      xml += `<email><![CDATA[${u.email}]]></email>`;
      xml += `<phone><![CDATA[${u.phone||''}]]></phone>`;
      xml += `<userType>${u.userType}</userType>`;
      xml += '</user>';
    });
    xml += '</users>';
    return xml;
  };

  const handleExportUsers = async (fmt) => {
    const base = `users${filter?`_${filter}`:''}`;
    try {
      if (fmt === 'csv') {
        const resp = await userApi.get('/export/csv', { params: filter?{type:filter}:{}, responseType: 'blob' });
        downloadBlob(new Blob([resp.data],{type:'text/csv'}), `${base}.csv`);
      } else if (fmt === 'json') {
        downloadBlob(new Blob([JSON.stringify(users,null,2)],{type:'application/json'}), `${base}.json`);
      } else if (fmt === 'xml') {
        downloadBlob(new Blob([usersToXml(users)],{type:'application/xml'}), `${base}.xml`);
      }
    } catch (err) {
      console.error(err);
      setUserError(t('usersExportError'));
    }
  };

  // Manager stats
  const [stats, setStats] = useState(null);
  const [animals, setAnimals] = useState([]);
  const [exemplars, setExemplars] = useState([]);
  const [managerError, setManagerError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const token = localStorage.getItem('token');
        const resp = await fetch('http://localhost:8081/api/animal/stats', { headers:{Authorization:`Bearer ${token}`} });
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        setStats(data);
      } catch (err) {
        console.error(err);
        setManagerError(t('statsLoadError'));
      }
    })();
  }, [t]);

  useEffect(() => {
    (async () => {
      try {
        setManagerError('');
        const [aRes,eRes] = await Promise.all([animalApi.get(''), exemplarApi.get('')]);
        setAnimals(aRes.data);
        setExemplars(eRes.data);
      } catch (err) {
        console.error(err);
        setManagerError(t('resourcesLoadError'));
      }
    })();
  }, [t]);

  const handleDeleteAnimal = async id => {
    if (!window.confirm(t('confirmDeleteAnimal'))) return;
    try { await animalApi.delete(`/${id}`); setAnimals(a=>a.filter(x=>x.id!==id)); } catch(err){console.error(err);}
  };
  const handleDeleteExemplar = async id => {
    if (!window.confirm(t('confirmDeleteExemplar'))) return;
    try { await exemplarApi.delete(`/${id}`); setExemplars(e=>e.filter(x=>x.id!==id)); } catch(err){console.error(err);}
  };

  const handleExportAnimal = () => { window.open('http://localhost:8081/api/animal/stats/export','_blank'); };

  return (
    <div className="admin-container">
      <Header />
      <h1 className="admin-title">{t('adminDashboard')}</h1>

      {/* Users Section */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">{t('userManagement')}</h2>
          <div className="controls">
            <label htmlFor="filter">{t('filterByType')}:</label>
            <select id="filter" value={filter} onChange={e=>setFilter(e.target.value)} className="select">
              <option value="">{t('all')}</option>
              <option value="VISITOR">{t('visitor')}</option>
              <option value="EMPLOYEE">{t('employee')}</option>
              <option value="MANAGER">{t('manager')}</option>
              <option value="ADMIN">{t('admin')}</option>
            </select>
            {['csv','json','xml'].map(fmt=>(
              <button key={fmt} onClick={()=>handleExportUsers(fmt)} className="btn export-btn">
                {t(`export${fmt.toUpperCase()}`)}
              </button>
            ))}
            <button onClick={()=>navigate('/admin/create')} className="btn add-btn">
              {t('createUser')}
            </button>
          </div>
        </div>
        {userError && <div className="error">{userError}</div>}
        {userLoading ? <div>{t('loadingUsers')}</div> : (
          <table className="data-table">
            <thead>
              <tr>
                <th>{t('id')}</th>
                <th>{t('username')}</th>
                <th>{t('email')}</th>
                <th>{t('role')}</th>
                <th>{t('actions')}</th>
              </tr>
            </thead>
            <tbody>
              {users.length ? users.map(u=>(
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.username}</td>
                  <td>{u.email}</td>
                  <td>{u.userType}</td>
                  <td className="actions">
                    <button onClick={()=>navigate(`/user/${u.id}`)} className="btn edit-btn">{t('edit')}</button>
                    <button onClick={()=>handleDeleteUser(u.id)} className="btn delete-btn">{t('delete')}</button>
                  </td>
                </tr>
              )) : <tr><td colSpan={5} className="no-data">{t('noUsers')}</td></tr>}
            </tbody>
          </table>
        )}
      </section>

      {/* Manager Section */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">{t('managerDashboard')}</h2>
          <button onClick={handleExportAnimal} className="btn export-btn">{t('exportToWord')}</button>
        </div>
        {managerError && <div className="error">{managerError}</div>}
        {!stats ? <div>{t('loadingStats')}</div> : (
          <div className="chart-grid">
            <div className="chart-section">
              <h3 className="chart-title">{t('animalsByCategory')}</h3>
              <Bar data={{ labels: Object.keys(stats.countPerCategory||{}), datasets:[{ label:t('animalsPerCategory'), data:Object.values(stats.countPerCategory||[]), backgroundColor:['#3182ce','#63b3ed','#90cdf4','#bee3f8'] }]}} />
            </div>
            <div className="chart-section">
              <h3 className="chart-title">{t('dietTypeDistribution')}</h3>
              <Pie data={{ labels: Object.keys(stats.dietDistribution||{}), datasets:[{ label:t('dietTypeDistribution'), data:Object.values(stats.dietDistribution||[]), backgroundColor:['#f56565','#ed8936','#ecc94b','#48bb78'] }]}} />
            </div>
          </div>
        )}
      </section>

      {/* Animal & Exemplar Management */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">{t('animalManagement')}</h2>
          <button onClick={()=>navigate('/animal/new')} className="btn add-btn">{t('addNewAnimal')}</button>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('id')}</th><th>{t('name')}</th><th>{t('category')}</th><th>{t('diet')}</th><th>{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {animals.map(a=>(<tr key={a.id}><td>{a.id}</td><td>{a.name}</td><td>{a.category}</td><td>{a.dietType}</td><td className="actions"><button onClick={()=>navigate(`/animal/${a.id}`)} className="btn edit-btn">{t('edit')}</button><button onClick={()=>handleDeleteAnimal(a.id)} className="btn delete-btn">{t('delete')}</button></td></tr>))}
          </tbody>
        </table>

        <div className="section-header">
          <h2 className="section-title">{t('exemplarManagement')}</h2>
          <button onClick={()=>navigate('/exemplar/new')} className="btn add-btn">{t('addNewExemplar')}</button>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('id')}</th><th>{t('name')}</th><th>{t('animalId')}</th><th>{t('location')}</th><th>{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {exemplars.map(e=>(<tr key={e.id}><td>{e.id}</td><td>{e.name}</td><td>{e.animalId}</td><td>{e.location}</td><td className="actions"><button onClick={()=>navigate(`/exemplar/${e.id}`)} className="btn edit-btn">{t('edit')}</button><button onClick={()=>handleDeleteExemplar(e.id)} className="btn delete-btn">{t('delete')}</button></td></tr>))}
          </tbody>
        </table>
      </section>
    </div>
  );
}
