import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  fetchCommandesParSemaine,
  fetchRecompensesParSemaine,
  fetchComparaisonSemaines,
} from "../../historiqueFidelite/historiqueFideliteSlice";
import "./dashboardAdmin.css";
import CommandesChart from "../../charts/CommandesChart";

const DashboardAdmin = () => {
  const dispatch = useDispatch();

  const {
    commandesParSemaine,
    recompensesParSemaine,
    comparaisonSemaines,
    loading,
    error,
  } = useSelector((state) => state.historiqueFidelite);
  const data = {
    labels: ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"],
    datasets: [
      {
        label: "Commandes",
        data: [12, 19, 3, 5, 2],
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        borderColor: "rgba(75, 192, 192, 1)",
        borderWidth: 1,
      },
    ],
  };
  useEffect(() => {
    const dateDebut = "2024-12-01"; // Exemple de date
    dispatch(fetchCommandesParSemaine(dateDebut));
    dispatch(fetchRecompensesParSemaine(dateDebut));
    dispatch(fetchComparaisonSemaines(dateDebut));
  }, [dispatch]);

  if (loading) return <p>Chargement...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div className="container-dashboard-admin">
    {/* Sidebar */}
    <div className="sidebar-dashboard-admin">
      <ul>
        <li><a href="/admin/dashboard">Dashboard</a></li>
        <li><a href="/admin/ecommerce">Ecommerce</a></li>
        <li><a href="/admin/analytics">Analytics</a></li>
        <li><a href="/admin/settings">Settings</a></li>
      </ul>
    </div>

    {/* Main content */}
    <div>
      {/* Header */}
      <div className="header-dashboard-admin">
        Tableau de Bord Admin
      </div>

      {/* Main dashboard content */}
      <div className="main-dashboard-admin">
        {/* Commandes par Semaine */}
        <div className="card-dashboard-admin">
          <h2>Commandes par Semaine</h2>
          <p>2,560 commandes cette semaine</p>
          <div className="chart">[Graphique ici]</div>
        </div>

        {/* Récompenses par Semaine */}
        <div className="card-dashboard-admin">
          <h2>Récompenses par Semaine</h2>
          <p>125 récompenses accordées</p>
          <div className="chart">[Graphique ici]</div>
        </div>

        {/* Comparaison Semaines */}
        <div className="card-dashboard-admin">
          <h2>Comparaison avec la Semaine Précédente</h2>
          <p>+15% par rapport à la semaine dernière</p>
          <div className="chart">[Graphique ici]</div>
        </div>
      </div>
    </div>
  </div>
  );
};

export default DashboardAdmin;
