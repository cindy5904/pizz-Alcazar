import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchPanierByUserId, supprimerPanier, reinitialiserPanier } from "./panierSlice";
import {
  reduireQuantiteItem,
  supprimerItem, ajouterQuantiteItem
} from "../panierItem/panierItemSlice";
import { creerCommande } from "../commande/commandeSlice";
import Header from "../../shared/header/Header";
import Navbar from "../../shared/navbar/Navbar";
import { useLocation, useNavigate } from "react-router-dom";
import "./panier.css";
import Footer from "../../shared/footer/Footer";

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
const calculateTotal = () => {
  if (!panier || !panier.itemsPanier) return 0;

  return panier.itemsPanier.reduce((total, item) => {
    return total + item.produit.prix * item.quantite;
  }, 0);
};
const handleIncreaseQuantity = (panierId, produitId) => {
  dispatch(
    ajouterQuantiteItem({
      panierId: panierId,
      produitId: produitId,
    })
  ).then(() => dispatch(fetchPanierByUserId(userId)));
};




  return (
    <>
      
      <Navbar />

      <div className="panier-container">
  <h1 className="panier-title">Mon Panier</h1>
  {panier && panier.itemsPanier && panier.itemsPanier.length > 0 ? (
    <div className="panier-items">
      {panier.itemsPanier.map((item) => (
       <div className="panier-item" key={item.id}>
       <div className="panier-item-details">
         <h2 className="panier-item-title">{item.produit.nom}</h2>
       </div>
       <div className="panier-item-actions">
         <button
           className="btn-symbol"
           onClick={() => handleReduceQuantity(item.produit.id, item.quantite)}
         >
           -
         </button>
         <span className="quantity-display">{item.quantite}</span>
         <button
    className="btn-symbol"
    onClick={() => handleIncreaseQuantity(panier.id, item.produit.id)}
  >
    +
  </button>

         <button
           className="btn-symbol btn-delete"
           onClick={() => handleDeleteItem(item.produit.id)}
         >
           X
         </button>
       </div>
     
       {/* Nom du Produit */}
       
     
       {/* Prix */}
       <p className="panier-item-price">{item.produit.prix}€</p>
     </div>
     
      ))}
    </div>
  ) : (
    <p className="panier-empty">Votre panier est vide.</p>
  )}
  <div className="panier-summary">
    <h2>Total : {calculateTotal().toFixed(2)}€</h2>
    <button className="btn-proceed" onClick={handleProceedToPayment}>
      Procéder à la commande
    </button>
  </div>
</div>
      <Footer/>
    </>
  );
};

export default Panier;
