import React, { useEffect } from "react"
import Header from "../../../shared/header/Header"
import Navbar from "../../../shared/navbar/Navbar"
import { obtenirProduitsDisponibles } from "../produitSlice";
import { useDispatch, useSelector } from "react-redux";

const ProduitListe = () => {
    const dispatch = useDispatch();
    const produits = useSelector((state) => state.produit.items); // Accéder à la liste des produits dans le state
    const chargement = useSelector((state) => state.produit.chargement);
    const erreur = useSelector((state) => state.produit.erreur);

    useEffect(() => {
        dispatch(obtenirProduitsDisponibles()); // Appeler l'action pour récupérer les produits
    }, [dispatch]);

    return (
        <>
            <Header />
            <Navbar />
            <h1>Liste des produits</h1>
            {chargement && <p>Chargement des produits...</p>} {/* Message de chargement */}
            {erreur && <p>Erreur lors de la récupération des produits: {erreur}</p>} {/* Gestion des erreurs */}
            <div className="produit-list">
                {produits.length > 0 ? (
                    produits.map((produit) => (
                        <div key={produit.id} className="produit-card">
                            <h2>{produit.nom}</h2>
                            <p>{produit.description}</p>
                            <p>Prix: {produit.prix} €</p>
                            <p>Disponibilité: {produit.disponibilite ? "Disponible" : "Indisponible"}</p>
                            <img src={produit.imagePath} alt={produit.nom} className="produit-image" /> {/* Affichage de l'image */}
                        </div>
                    ))
                ) : (
                    <p>Aucun produit disponible.</p> // Message si aucun produit n'est trouvé
                )}
            </div>
        </>
    );
}

export default ProduitListe;