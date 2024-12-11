import React from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../../shared/navbar/Navbar";
import Footer from "../../../shared/footer/Footer";
import { useSelector } from "react-redux";
import "./success.css";

const Success = () => {
    const navigate = useNavigate();

    // Récupérer les détails du paiement via Redux
    const { montantPaye, date, numeroReference } = useSelector(
      (state) => state.paiement?.paiementActuel || {}
    );

  return (
    <>
    <div className="success-container">
      <div className="success-card">
        <div className="success-icon">
          <span>&#10004;</span> {/* Checkmark icon */}
        </div>
        <h1 className="h1-success">Paiement réussi</h1>
        <p className="p-success">Merci pour votre achat !</p>
        <div className="success-details">
          <div className="success-detail-row">
            <span>Montant payé :</span>
            <span>{montantPaye ? `${montantPaye.toFixed(2)} €` : "N/A"}</span>
          </div>
          <div className="success-detail-row">
            <span>Date & Heure :</span>
            <span>{date || "N/A"}</span>
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
    </>
  );
};

export default Success;
