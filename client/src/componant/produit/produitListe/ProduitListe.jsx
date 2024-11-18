import React, { useEffect } from "react";
import Header from "../../../shared/header/Header";
import Navbar from "../../../shared/navbar/Navbar";
import { obtenirProduitsDisponibles, supprimerProduit } from "../produitSlice";
import { useDispatch, useSelector } from "react-redux";
import "./produitList.css";
import { useNavigate } from "react-router-dom";
import { ajouterOuMettreAJourItem, fetchPanierByUserId } from '../../panier/panierSlice';



const ProduitListe = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const produits = useSelector((state) => state.produit.items);
  const chargement = useSelector((state) => state.produit.chargement);
  const erreur = useSelector((state) => state.produit.erreur);
  const panier = useSelector((state) => state.panier.panier);
  const user = useSelector((state) => state.auth.user);
  const roles = user ? user.roles : [];
  console.log("Rôles de l'utilisateur dans ProduitListe :", roles);

  useEffect(() => {
    dispatch(obtenirProduitsDisponibles()); 
  }, [dispatch]);

  const handleAddToCart = (produitId) => {
    if (!user) {
        console.error("Utilisateur non authentifié");
        return; // Ne pas procéder si l'utilisateur n'est pas authentifié
    }

    console.log("ID de l'utilisateur :", user.id);

    const itemData = {
        produitId: produitId,
        quantite: 1 // Quantité par défaut pour chaque ajout
    };

    // Appel pour ajouter ou mettre à jour l'item dans le panier de l'utilisateur
    dispatch(ajouterOuMettreAJourItem({ userId: user.id, produitId, quantite: 1 }))
        .then(response => {
            console.log("Panier mis à jour avec succès : ", response);
        })
        .catch(error => {
            console.error("Erreur lors de l'ajout au panier : ", error);
        });
};





  const handleEdit = (id) => {
    console.log(`Modifier le produit ${id}`);
    navigate(`/produits/modifier/${id}`); // Redirige vers le formulaire de produit avec l'ID
};

  const handleDelete = (produitId) => {
    if (window.confirm("Êtes-vous sûr de vouloir supprimer ce produit ?")) {
      dispatch(supprimerProduit(produitId));
    }
  };

  return (
    <>
    <Header />
        <Navbar />
        <div className="produit-container">
            <h1 className="produit-titre">Nos Délicieuses Pizzas</h1>
            {chargement && <div className="loading-spinner">Chargement des produits...</div>}
            {erreur && <p className="produit-erreur">Erreur lors de la récupération des produits: {erreur}</p>}

            <div className="produit-list">
                {produits.length > 0 ? (
                    produits.map((produit) => {
                        const imageUrl = produit.imagePath ? `http://localhost:8080${produit.imagePath}` : 'path/to/placeholder.jpg';
                        return (
                            <div key={produit.id} className="produit-card">
                                <div className="produit-image-container">
                                    <img src={imageUrl} alt={produit.nom} className="produit-image" />
                                    
                                </div>
                                <div className="produit-details">
                                    <h2 className="produit-nom">{produit.nom}</h2>
                                    <p className="produit-description">{produit.description}</p>
                                    <div className="produit-prix">
                                        <span className="produit-prix-actuel">{produit.prix} €</span>
                                    </div>
                                    {Array.isArray(roles) && roles.includes("ROLE_ADMIN") ? (
                                        <div className="produit-actions">
                                            <button onClick={() => handleEdit(produit.id)} className="btn btn-edit">Modifier</button>
                                            <button onClick={() => handleDelete(produit.id)} className="btn btn-delete">Supprimer</button>
                                        </div>
                                    ) : (
                                        <button onClick={() => handleAddToCart(produit.id)} className="btn btn-add">Ajouter au panier</button>
                                    )}
                                </div>
                            </div>
                        );
                    })
                ) : (
                    <p className="produit-vide">Aucune pizza disponible.</p>
                )}
            </div>
        </div>
    </>
  );
};

export default ProduitListe;
