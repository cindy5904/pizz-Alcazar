import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { creerPaiement } from "../paiement/paiementSlice";
import { checkUser } from "../auth/authSlice";
import Header from "../../shared/header/Header";
import Navbar from "../../shared/navbar/Navbar";
import "./paiement.css";

const PagePaiement = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { commandeId, montantTotal, moyenPaiement } = location.state || {};
  const [paiementStatut, setPaiementStatut] = useState(null);

  useEffect(() => {
    if (!commandeId || !montantTotal || !moyenPaiement) {
      alert("Informations de paiement manquantes !");
      navigate("/paiement");
    }
  }, [commandeId, montantTotal, moyenPaiement, navigate]);

  const handleValiderPaiement = async () => {
    const donneesPaiement = {
      montant: montantTotal,
      statut: "REUSSI", 
      moyenPaiement,
      datePaiement: new Date().toISOString(),
      commandeId,
    };
  
    try {
      const response = await dispatch(creerPaiement(donneesPaiement)).unwrap();
      alert(`Paiement ${response.statut === "REUSSI" ? "réussi" : "échoué"} !`);
  
      if (response.statut === "REUSSI") {
        console.log("Paiement réussi. Mise à jour des données utilisateur.");
        const updatedUser = await dispatch(checkUser()).unwrap();
        console.log("Utilisateur mis à jour après paiement :", updatedUser);
        navigate("/mon-compte");
      }
      
    } catch (error) {
      alert("Erreur lors du paiement !");
      console.error(error);
    }
  };
  

  return (
    <>
    
    <Navbar/>
    <div className="page-paiement-container">
        <h1>Page de Paiement</h1>
        <p>Montant à payer : <strong>{montantTotal}€</strong></p>
        <p>Moyen de paiement choisi : <strong>{moyenPaiement}</strong></p>
        <button className="btn-valider" onClick={handleValiderPaiement}>
          Valider le paiement
        </button>
        {paiementStatut && (
          <p className="paiement-statut">
            Statut du paiement :{" "}
            <span style={{ color: paiementStatut === "REUSSI" ? "green" : "red" }}>
              {paiementStatut}
            </span>
          </p>
        )}
      </div>
    </>
  );
};

export default PagePaiement;
