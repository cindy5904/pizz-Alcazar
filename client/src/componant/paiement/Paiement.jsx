import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { obtenirPaiementParId, creerPaiement, createPayPalOrder } from "../paiement/paiementSlice";
import { mettreAJourCommande, obtenirCommandeParId } from "../commande/commandeSlice";
import Navbar from "../../shared/navbar/Navbar";
import Footer from "../../shared/footer/Footer";
import paypal from "../../assets/images/paypal.png";
import visa from "../../assets/images/visa.png";
import cb from "../../assets/images/cb.png";

const Paiement = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);
  const commande = useSelector((state) => state.commandes?.commandeActuelle || null);
  console.log("Commande actuelle dans Paiement.jsx via useSelector :", commande);
  const stateCommandes = useSelector((state) => state.commandes);
console.log("État global des commandes dans Redux :", stateCommandes);

  const [moyenPaiement, setMoyenPaiement] = useState("carte");
  const [paypalOrderId, setPaypalOrderId] = useState(null);
  const location = useLocation();
  const commandeId = location.state?.commandeId;
  const chargement = useSelector((state) => state.commandes?.chargement ?? false);
  
  
  useEffect(() => {
    if (!commande && commandeId) {
        dispatch(obtenirCommandeParId(commandeId));
    }
}, [dispatch, commande, commandeId]);

  useEffect(() => {
    console.log("Commande dans Paiement.jsx après Redux :", commande);
}, [commande]);

console.log("Utilisateur récupéré dans Paiement.jsx :", user);
console.log("Commande récupérée dans Paiement.jsx :", commande);
console.log("CommandeId récupérée dans Paiement.jsx :", commandeId);
  const montantTotal = commande?.itemsCommande.reduce(
    (total, item) => total + item.produitPrix * item.quantite,
    0
  );

  const handlePaiement = async () => {
    try {
        const orderId = await dispatch(createPayPalOrder(commande.id)).unwrap();
        window.location.href = `https://www.sandbox.paypal.com/checkoutnow?token=${orderId}`;
    } catch (error) {
        console.error("Erreur lors de la création de la commande PayPal :", error);
        alert("Une erreur est survenue lors de la création de la commande PayPal.");
    }
};



  if (chargement) {
    return <p>Chargement de la commande...</p>;
  }

  return (
    <>
    <Navbar/>
    <div className="paiement-container">
        <h1 className="paiement-title">Paiement</h1>
        {commande ? (
          <div className="paiement-recap">
            <h2>Récapitulatif de la commande</h2>
            <ul>
              {commande.itemsCommande.map((item) => (
                <li key={item.id}>
                  {item.produitNom} - {item.quantite} x {item.produitPrix}€
                </li>
              ))}
            </ul>
            <h3 className="paiement-total">Montant total : {montantTotal}€</h3>
            <div className="separator"></div>
            <h2>Choisissez votre moyen de paiement</h2>
            <div className="paiement-modes">
  <button
    className={`paiement-mode ${moyenPaiement === 'carte' ? 'active' : ''}`}
    onClick={() => setMoyenPaiement('carte')}
  >
    <img
      src={cb}
      alt="Carte bancaire"
      className="paiement-image"
    />
    <span>Carte bancaire</span>
  </button>
  <button
    className={`paiement-mode ${moyenPaiement === 'visa' ? 'active' : ''}`}
    onClick={() => setMoyenPaiement('visa')}
  >
    <img
      src={visa}
      alt="Visa"
      className="paiement-image"
    />
    <span>Carte bancaire</span>
  </button>

  <button
    className={`paiement-mode ${moyenPaiement === 'paypal' ? 'active' : ''}`}
    onClick={() => setMoyenPaiement('paypal')}
  >
    <img
      src={paypal}
      alt="PayPal"
      className="paypal"
    />
    <span>PayPal</span>
  </button>
</div>

            <button className="paiement-button" onClick={handlePaiement}>
              Confirmer le paiement
            </button>
          </div>
        ) : (
          <p className="paiement-no-order">Aucune commande en cours.</p>
        )}
      </div>
     <Footer/> 
    </>
  );
};

export default Paiement;
