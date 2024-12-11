import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchPanierByUserId, supprimerPanier, reinitialiserPanier } from "./panierSlice";
import {
  reduireQuantiteItem,
  supprimerItem, ajouterQuantiteItem
} from "../panierItem/panierItemSlice";
import { creerCommande } from "../commande/commandeSlice";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "./panier.css";
import trash from "../../assets/images/trash.png";

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
    console.log("Panier ID :", panier.id);
    console.log("Produit ID :", produitId);
    dispatch(supprimerItem({ panierId: panier.id, produitId })).then(() =>
       dispatch(fetchPanierByUserId(userId, produitId))); 
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
const calculateTotalHT = () => {
  if (!panier || !panier.itemsPanier) return 0;

  return panier.itemsPanier.reduce((totalHT, item) => {
    const tauxTVA = item.produit.tauxTVA || 0.2; 
    const prixHT = item.produit.prix / (1 + tauxTVA); 
    return totalHT + prixHT * item.quantite;
  }, 0);
};

const calculateTVA = () => {
  if (!panier || !panier.itemsPanier) return 0;

  return panier.itemsPanier.reduce((totalTVA, item) => {
    const tauxTVA = item.produit.tauxTVA || 0.2; 
    const montantTVA = item.produit.prix * (tauxTVA / (1 + tauxTVA)); 
    return totalTVA + montantTVA * item.quantite;
  }, 0);
};

const calculateTotalTTC = () => {
  if (!panier || !panier.itemsPanier) return 0;

  return panier.itemsPanier.reduce((total, item) => {
    return total + item.produit.prix * item.quantite;
  }, 0);
};
const calculateFidelityPoints = () => {
  const total = calculateTotal();
  return Math.floor(total); 
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
   <div className="panier-container">
  <Link to="/categories" className="continue-shopping">
    &larr; <strong>Continuer mes achats</strong>
  </Link>

  <h1 className="panier-title">Mon Panier</h1>

  {panier && panier.itemsPanier && panier.itemsPanier.length > 0 ? (
    <div className="panier-items">
      {panier.itemsPanier.map((item) => (
        <div className="panier-item" key={item.id}>
          <div className="panier-item-header">
          <div className="panier-item-title-container">
    <h2 className="panier-item-title">{item.produit.nom}</h2>
  </div>
            

            <div className="panier-item-actions">
              <button className="btn-symbol" onClick={() => handleReduceQuantity(item.produit.id, item.quantite)}>-</button>
              <span className="quantity-display">{item.quantite}</span>
              <button className="btn-symbol" onClick={() => handleIncreaseQuantity(panier.id, item.produit.id)}>+</button>
            </div>
            <div className="section-gauche-item">
            <p className="panier-item-price">{(item.produit.prix * item.quantite).toFixed(2)}€</p>
            <button className="btn-trash" onClick={() => handleDeleteItem(item.produit.id)}>
  <img src={trash} alt="poubelle de suppression article" className="trash" />
</button>

            </div>
           
            <details className="panier-item-details">
              <summary className="panier-item-summary">Description</summary>
              <hr className="panier-item-separator" />
              <p className="panier-item-description">
                {item.produit.description || "Aucune description disponible."}
              </p>
              <button className="btn-modifier" onClick={() => navigate("/categories")}>Modifier</button>
            </details>
          </div>
        </div>
      ))}
    </div>
  ) : (
    <p className="panier-empty">Votre panier est vide.</p>
  )}
  <div className="fidelity-points-container">
  <p className="fidelity-points">
    Avec cette commande, vous allez gagner <strong>{calculateFidelityPoints()}</strong> points de fidélité !<span className="icon-crown">✨</span>
  </p>
</div>
<div className="panier-summary">
  <div className="panier-summary-item">
    <span className="panier-summary-label">Total HT</span>
    <span className="panier-summary-value">{calculateTotalHT().toFixed(2)}€</span>
  </div>
  <div className="panier-summary-item">
    <span className="panier-summary-label">Montant TVA</span>
    <span className="panier-summary-value">{calculateTVA().toFixed(2)}€</span>
  </div>
  <hr className="panier-summary-separator" />
  <div className="panier-summary-total">
    <span className="panier-summary-total-label">Total</span>
    <span className="panier-summary-total-value">{calculateTotalTTC().toFixed(2)}€</span>
  </div>
  
  <button className="btn-proceed" onClick={handleProceedToPayment}>
    Valider mon panier
  </button>
</div>

</div>

 
    </>
  );
};

export default Panier;
