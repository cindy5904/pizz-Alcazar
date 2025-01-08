import React from "react";
import "./success.css";

import { useLocation, useNavigate } from "react-router-dom";

const Success = () => {
  const navigate = useNavigate();
    const location = useLocation();
    const { montant, datePaiement, numeroReference } = location.state || {}; // Récupération des données passées

    return (
        <div className="success-container">
            <div className="success-card">
                <div className="success-icon">
                    <span>&#10004;</span>
                </div>
                <h1 className="h1-success">Paiement réussi</h1>
                <p className="p-success">Merci pour votre achat !</p>
                <div className="success-details">
                    <div className="success-detail-row">
                        <span>Montant payé :</span>
                        <span>{montant ? `${montant.toFixed(2)} €` : "N/A"}</span>
                    </div>
                    <div className="success-detail-row">
                        <span>Date & Heure :</span>
                        <span>{datePaiement ? new Date(datePaiement).toLocaleString() : "N/A"}</span>
                    </div>
                    <div className="success-detail-row">
                        <span>Numéro de référence :</span>
                        <span>{numeroReference || "N/A"}</span>
                    </div>
                </div>
                <button className="btn-success" onClick={() => navigate("/")}>
                    Retourner à l'accueil
                </button>
            </div>
        </div>
    );
};

export default Success;

