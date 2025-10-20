// src/App.jsx
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import MainPage       from './pages/MainPage';
import Login          from './pages/Login';
import Home           from './pages/Home';
import EmployeePage   from './pages/EmployeePage';
import AnimalForm     from './pages/AnimalForm';
import ExemplarForm   from './pages/ExemplarForm';
import ProtectedRoute from './components/common/ProtectedRoute';
import ManagerPage from './pages/ManagerPage';
import AdminPage from './pages/AdminPage';
import EditUserPage from './pages/EditUserPage';
import CreateUserPage from './pages/CreateUserPage';


export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"       element={<MainPage />} />
        <Route path="/login"  element={<Login />} />
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />
        <Route
          path="/employee/*"
          element={
            <ProtectedRoute>
              <EmployeePage />
            </ProtectedRoute>
          }
        />

        {/* Animal CRUD */}
        <Route
          path="/animal/new"
          element={
            <ProtectedRoute>
              <AnimalForm isEdit={false} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/animal/:id"
          element={
            <ProtectedRoute>
              <AnimalForm isEdit={true} />
            </ProtectedRoute>
          }
        />

        {/* Exemplar CRUD */}
        <Route
          path="/exemplar/new"
          element={
            <ProtectedRoute>
              <ExemplarForm isEdit={false} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/exemplar/:id"
          element={
            <ProtectedRoute>
              <ExemplarForm isEdit={true} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/manager/*"
          element={
          <ProtectedRoute>
            <ManagerPage />
          </ProtectedRoute>
        }
      />
      <Route
          path="/admin"
          element={
          <ProtectedRoute>
            <AdminPage />
          </ProtectedRoute>
        }
      />
      <Route 
        path="/user/:id" 
        element={
        <ProtectedRoute>
            <EditUserPage />
        </ProtectedRoute>
        } 
      />

      <Route 
        path="/admin/create" 
        element={
        <ProtectedRoute>
            <CreateUserPage />
        </ProtectedRoute>
        } 
      />


      </Routes>
    </BrowserRouter>
  );
}
