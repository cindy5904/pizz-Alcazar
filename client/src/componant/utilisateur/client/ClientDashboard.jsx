import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { recupererHistoriqueRecompensesThunk } from '../../recompense/recompenseSlice';
import "../client/clientdashboard.css";


const ClientDashboard = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const recompenses = useSelector((state) => state.recompense.recompenses);
  const chargementRecompenses = useSelector((state) => state.recompense.chargement);

  useEffect(() => {
    if (user?.id) {
      dispatch(recupererHistoriqueRecompensesThunk(user.id));
    }
  }, [dispatch, user]);
  
  if (!user) {
    return <p>Chargement de vos informations...</p>;
  }
  const progressPercentage = Math.min(
    (user.pointsFidelite / 100) * 100,
    100
  );

  return (
    <>
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
      <div className="recompenses-section">
        <h2>Vos récompenses obtenues</h2>
        {chargementRecompenses ? (
          <p>Chargement de vos récompenses...</p>
        ) : recompenses.length > 0 ? (
          <ul className="recompenses-list">
            {recompenses.map((recompense) => (
              <li key={recompense.id} className="recompense-item">
                <p><strong>{recompense.nom}</strong></p>
                <p>{recompense.description}</p>
                <p>Date obtenue : {new Date(recompense.dateRemise).toLocaleDateString()}</p>
                <p>Code : <strong>{recompense.codeRemise}</strong></p>
              </li>
            ))}
          </ul>
        ) : (
          <p>Vous n'avez pas encore obtenu de récompenses.</p>
        )}
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
