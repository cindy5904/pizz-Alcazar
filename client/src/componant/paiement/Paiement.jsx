import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { obtenirPaiementParId, creerPaiement } from "../paiement/paiementSlice";
import { mettreAJourCommande, obtenirCommandeParId } from "../commande/commandeSlice";
import Header from "../../shared/header/Header";
import Navbar from "../../shared/navbar/Navbar";

const Paiement = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);
  const commande = useSelector((state) => state.commandes?.commandeActuelle || null);
  console.log("Commande actuelle dans Paiement.jsx via useSelector :", commande);
  const stateCommandes = useSelector((state) => state.commandes);
console.log("État global des commandes dans Redux :", stateCommandes);

  const [moyenPaiement, setMoyenPaiement] = useState("carte");
  const [coordonneesBancaires, setCoordonneesBancaires] = useState({
    numeroCarte: "",
    dateExpiration: "",
    cvv: "",
  });
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

  const handlePaiement = () => {
    if (!commande || !montantTotal) {
        console.log("Aucune commande valide.");
        return;
    }

    
    navigate("/page-paiement", {
        state: {
            commandeId: commande.id, 
            montantTotal: montantTotal, // Montant total
            moyenPaiement: moyenPaiement, // Moyen de paiement choisi
        },
    });
};

  if (chargement) {
    return <p>Chargement de la commande...</p>;
  }

  return (
    <>
    <Navbar/>
    <div>
      <h1>Paiement</h1>
      {commande ? (
        <div>
          <h2>Récapitulatif de la commande</h2>
          <ul>
            {commande.itemsCommande.map((item) => (
              <li key={item.id}>
                {item.produitNom} - {item.quantite} x {item.produitPrix}€
              </li>
            ))}
          </ul>
          <h3>Montant total : {montantTotal}€</h3>

          <h2>Choisissez votre moyen de paiement</h2>
          <select
            value={moyenPaiement}
            onChange={(e) => setMoyenPaiement(e.target.value)}
          >
            <option value="carte">Carte bancaire</option>
            <option value="paypal">PayPal</option>
          </select>

          <button onClick={handlePaiement}>Confirmer le paiement</button>
        </div>
      ) : (
        <p>Aucune commande en cours.</p>
      )}
    </div>
    </>
  );
};

export default Paiement;
