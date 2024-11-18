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

  const [moyenPaiement, setMoyenPaiement] = useState("carte");
  const location = useLocation();
  const commandeId = location.state?.commandeId;
  const chargement = useSelector((state) => state.commandes?.chargement ?? false);
  
  
  useEffect(() => { if (commandeId) { dispatch(obtenirCommandeParId(commandeId)); } }, [dispatch, commandeId]);

console.log("Utilisateur récupéré dans Paiement.jsx :", user);
console.log("Commande récupérée dans Paiement.jsx :", commande);
console.log("CommandeId récupérée dans Paiement.jsx :", commandeId);
  // Calcul du montant total de la commande
  const montantTotal = commande?.itemsCommande.reduce(
    (total, item) => total + item.produitPrix * item.quantite,
    0
  );

  // Fonction pour gérer la soumission du paiement
  const handlePaiement = () => {
    if (!commande || !montantTotal) {
      console.log("Aucune commande valide.");
      return;
    }

    // Préparer les données pour le paiement
    const donneesPaiement = {
      montant: montantTotal * 100, // En cents pour Stripe
      statut: "EN_ATTENTE",
      moyenPaiement: moyenPaiement,
      datePaiement: new Date().toISOString(),
      commandeId: commande.id,
    };

    // Créer le paiement et mettre à jour la commande
    dispatch(creerPaiement(donneesPaiement))
      .then(() => {
        dispatch(
          mettreAJourCommande({
            id: commande.id,
            updatedData: { statut: "PAYEE" },
          })
        );
        alert("Paiement réussi !");
        navigate("/commandes"); // Rediriger vers la page des commandes
      })
      .catch((error) => {
        alert("Le paiement a échoué.");
        console.error(error);
      });
  };

  if (chargement) {
    return <p>Chargement de la commande...</p>;
  }

  return (
    <>
    <Header/>
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
