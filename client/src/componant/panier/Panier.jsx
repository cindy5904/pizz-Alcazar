import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchPanierByUserId, supprimerPanier, reinitialiserPanier } from "./panierSlice";
import {
  reduireQuantiteItem,
  supprimerItem,
} from "../panierItem/panierItemSlice";
import { creerCommande } from "../commande/commandeSlice";
import Header from "../../shared/header/Header";
import Navbar from "../../shared/navbar/Navbar";
import { useLocation, useNavigate } from "react-router-dom";

const Panier = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const userId = user ? user.id : null;
  console.log("ID de l'utilisateur : ", userId);
  const panier = useSelector((state) => state.panier.panier);
  const loading = useSelector((state) => state.panier.loading);
  const error = useSelector((state) => state.panier.error);
  const navigate = useNavigate();
  
 
  useEffect(() => {
    console.log(
      "Tentative de récupération du panier pour l'utilisateur ID : ",
      userId
    );
    if (userId) {
      dispatch(fetchPanierByUserId(userId));
    }
  }, [dispatch, userId]);

  const handleDeleteItem = (produitId) => {
    dispatch(supprimerPanier({ panierId: panier.id, produitId }));
  };

  const handleReduceQuantity = (produitId, quantiteActuelle) => {
    if (quantiteActuelle > 1) {
      dispatch(
        reduireQuantiteItem({ panierId: panier.id, produitId, quantite: 1 })
      ).then(() => dispatch(fetchPanierByUserId(userId)));
    } else {
      dispatch(supprimerItem({ panierId: panier.id, produitId })).then(() =>
        dispatch(fetchPanierByUserId(userId))
      );
    }
  };
  const handleProceedToPayment = () => {
    if (!panier || !panier.itemsPanier || panier.itemsPanier.length === 0) {
        alert("Votre panier est vide.");
        return;
    }

    const statutInitial = "EN_COURS";
    const adresseLivraison = user.adresse || "Adresse par défaut";
    const telephone = user.telephone || "Numéro par défaut";

    const nouvelleCommande = {
        detailsCommande: `Commande de ${user.prenom} ${user.nom}`,
        statut: statutInitial,
        userId: userId,
        panierId: panier.id,
        adresseLivraison: adresseLivraison,
        telephone: telephone,
        typeLivraison: "standard",
    };

    console.log("Données de la nouvelle commande :", nouvelleCommande);

    dispatch(creerCommande(nouvelleCommande))
        .then((action) => {
            console.log("Réponse après création de la commande :", action);

            if (action.payload && action.payload.id) {
                console.log("Commande créée avec succès. ID de la commande :", action.payload.id);

                // Rediriger vers la page de paiement
                navigate("/paiement", { state: { commandeId: action.payload.id } });
            } else {
                console.log("Erreur lors de la création de la commande.");
                console.error("Erreur : action.payload est invalide", action.payload);
            }
        })
        .catch((error) => {
            console.error("Erreur lors de la création de la commande :", error);
            console.log("Erreur lors de la création de la commande.");
        });
};

  return (
    <>
      <Header />
      <Navbar />

      <div>
        <h1>Mon Panier</h1>
        {panier && panier.itemsPanier && panier.itemsPanier.length > 0 ? (
          <ul>
            {panier.itemsPanier.map((item) => (
              <li key={item.id}>
                <h2>{item.produit.nom}</h2>
                <p>Quantité : {item.quantite}</p>
                <button
                  onClick={() =>
                    handleReduceQuantity(item.produit.id, item.quantite)
                  }
                >
                  Réduire
                </button>

                <button onClick={() => handleDeleteItem(item.produit.id)}>
                  Supprimer
                </button>
              </li>
            ))}
          </ul>
        ) : (
          <p>Votre panier est vide.</p>
        )}
        <button onClick={handleProceedToPayment}>Procéder à la commande</button>

      </div>
    </>
  );
};

export default Panier;
