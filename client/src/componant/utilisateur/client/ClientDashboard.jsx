import React from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import "../client/clientdashboard.css";
import Header from '../../../shared/header/Header';
import Navbar from '../../../shared/navbar/Navbar';

const ClientDashboard = () => {
  const user = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  
  if (!user) {
    return <p>Chargement de vos informations...</p>;
  }
  const progressPercentage = Math.min(
    (user.pointsFidelite / 100) * 100,
    100
  );

  return (
    <>
    <Header/>
    <Navbar/>
    <div className="client-dashboard">
      <h1>Bonjour, {user.prenom} {user.nom} 👋</h1>

      <div className="info-section">
        <h2>Informations personnelles</h2>
        <p><strong>Email :</strong> {user.email}</p>
        <p><strong>Adresse :</strong> {user.adresse || "Non renseignée"}</p>
        <p><strong>Téléphone :</strong> {user.telephone || "Non renseigné"}</p>
        <button
          onClick={() => navigate("/mon-compte/modifier")}
          className="btn btn-secondary"
        >
          Modifier mes informations
        </button>
      </div>

      <div className="points-section">
        <h2>Points de fidélité</h2>
        <p>Vous avez <strong>{user.pointsFidelite}</strong> points.</p>
        <p>Prochaine récompense dans {100 - user.pointsFidelite} points.</p>
        <div className="points-progress">
          <div
            className="points-progress-bar"
            style={{ width: `${progressPercentage}%` }}
          ></div>
        </div>
      </div>

      <div className="actions-section">
        <button
          onClick={() => navigate("/commande")}
          className="btn btn-primary"
        >
          Voir mes commandes
        </button>
      </div>
    </div>
    </>
  );
};

export default ClientDashboard;
