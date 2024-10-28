import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { registerUser } from './authSlice';
import { useNavigate } from 'react-router-dom';
import '../auth/register.css'
import Header from '../../shared/header/Header';
import Navbar from '../../shared/navbar/Navbar';

const Register = () => {
    // État pour chaque champ du formulaire
    const [nom, setNom] = useState('');
    const [prenom, setPrenom] = useState('');
    const [email, setEmail] = useState('');
    const [motDePasse, setMotDePasse] = useState('');
    const [adresse, setAdresse] = useState('');
    const [telephone, setTelephone] = useState('');
    
    const dispatch = useDispatch();
    const loading = useSelector((state) => state.auth.loading);
    const error = useSelector((state) => state.auth.error);
    const navigate = useNavigate();
  
    const handleSubmit = (e) => {
      e.preventDefault();
      // Créer un objet userData avec tous les champs requis
      const userData = {
        nom,
        prenom,
        email,
        motDePasse,
        adresse,
        telephone,
      };
      dispatch(registerUser(userData));
      navigate('/login');
    };
  
    return (
        <>
        <Header/>

        <div className="register-container">
        <h2>Inscription</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label>Nom :</label>
            <input
              type="text"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Prénom :</label>
            <input
              type="text"
              value={prenom}
              onChange={(e) => setPrenom(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Email :</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Mot de passe :</label>
            <input
              type="password"
              value={motDePasse}
              onChange={(e) => setMotDePasse(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Adresse :</label>
            <input
              type="text"
              value={adresse}
              onChange={(e) => setAdresse(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Téléphone :</label>
            <input
              type="text"
              value={telephone}
              onChange={(e) => setTelephone(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? 'Chargement...' : 'S\'inscrire'}
          </button>
        </form>
      </div>
      </>
    );
  };

export default Register;
