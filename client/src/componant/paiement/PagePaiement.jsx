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

  const { commandeId, paypalOrderId } = location.state || {};
  const [paiementStatut, setPaiementStatut] = useState(null);

  useEffect(() => {
    if (!commandeId || !paypalOrderId) {
      alert("Informations de paiement manquantes !");
      navigate("/paiement");
    }
  }, [commandeId, paypalOrderId, navigate]);

  const handleCapturePayPal = async () => {
    try {
      const response = await dispatch(
        capturePayPalOrder({ orderId: paypalOrderId, commandeId })
      ).unwrap();

      alert(`Paiement ${response.statut === "REUSSI" ? "réussi" : "échoué"} !`);
      setPaiementStatut(response.statut);

      if (response.statut === "REUSSI") {
        navigate("/mon-compte");
      }
    } catch (error) {
      alert("Erreur lors de la capture du paiement PayPal !");
      console.error(error);
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-paiement-container">
        <h1>Validation du Paiement</h1>
        <p>
          Redirection de PayPal terminée. Cliquez ci-dessous pour valider la
          transaction.
        </p>
        <button className="btn-valider" onClick={handleCapturePayPal}>
          Valider le paiement PayPal
        </button>
        {paiementStatut && (
          <p className="paiement-statut">
            Statut du paiement :{" "}
            <span
              style={{
                color: paiementStatut === "REUSSI" ? "green" : "red",
              }}
            >
              {paiementStatut}
            </span>
          </p>
        )}
      </div>
    </>
  );
};

export default PagePaiement;
