import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  fetchCommandesParSemaine,
  fetchRecompensesParSemaine,
  fetchComparaisonSemaines,
} from "../../historiqueFidelite/historiqueFideliteSlice";
import "./dashboardAdmin.css";


const DashboardAdmin = () => {
  const dispatch = useDispatch();
  const {
    commandesParSemaine,
    recompensesParSemaine,
    comparaisonSemaines,
    loading,
    error,
  } = useSelector((state) => state.historiqueFidelite);
  console.log("comparaisonSemaines:", comparaisonSemaines);


  useEffect(() => {
    
    const dateDebut = new Date().toISOString().split("T")[0];
    dispatch(fetchCommandesParSemaine(dateDebut));
    dispatch(fetchRecompensesParSemaine(dateDebut));
    dispatch(fetchComparaisonSemaines(dateDebut));
  }, [dispatch]);

  if (loading) return <p>Chargement...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div className="dashboard">
      <h1>Tableau de Bord Admin</h1>
      <div className="stats">
        <div className="stat-card">
          <h3>Commandes par Semaine</h3>
          <ul>
            {commandesParSemaine.map((count, index) => (
              <li key={index}>Jour {index + 1}: {count} commandes</li>
            ))}
          </ul>
        </div>
        <div className="stat-card">
          <h3>Récompenses par Semaine</h3>
          <p>{recompensesParSemaine} récompenses</p>
        </div>
        <div className="stat-card">
          <h3>Comparaison avec la Semaine Précédente</h3>
          <p>
  {comparaisonSemaines !== null && !isNaN(comparaisonSemaines)
    ? `${comparaisonSemaines > 0 ? "+" : ""}${comparaisonSemaines.toFixed(2)}% par rapport à la semaine dernière`
    : "Données non disponibles"}
</p>

        </div>
      </div>
    </div>
  );
};

export default DashboardAdmin;
