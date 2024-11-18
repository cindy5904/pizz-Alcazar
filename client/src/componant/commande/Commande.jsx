import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    obtenirToutesCommandes,
    creerCommande,
    obtenirCommandeParId,
    mettreAJourCommande,
    supprimerCommande,
    definirPage,
} from '../commande/commandeSlice';

const Commandes = () => {
    const dispatch = useDispatch();
    const utilisateur = useSelector((state) => state.auth.user);
    const panier = useSelector((state) => state.panier.panier);
    const { commandes, chargement, erreur, page, totalPages } = useSelector((state) => state.commandes);
    const [commandeId, setCommandeId] = useState('');
    const [detailsCommande, setDetailsCommande] = useState('');

    useEffect(() => {
        dispatch(obtenirToutesCommandes({ page }));
    }, [dispatch, page]);

    
    const handleObtenirCommandeParId = () => {
        if (commandeId) {
            dispatch(obtenirCommandeParId(commandeId));
        }
    };

    
    const handleCreerCommande = () => {
        const nouvelleCommande = {
            detailsCommande,
            statut: 'EN_COURS',
            userId: 1, // Exemple d'ID utilisateur
            panierId: 1, // Exemple d'ID panier
        };
        dispatch(creerCommande(nouvelleCommande));
        setDetailsCommande('');
    };

    // Gérer la mise à jour d'une commande
    const handleMettreAJourCommande = (id) => {
        const donneesMiseAJour = {
            detailsCommande: 'Commande mise à jour',
            statut: 'LIVREE',
        };
        dispatch(mettreAJourCommande({ id, donneesMiseAJour }));
    };

    // Gérer la suppression d'une commande
    const handleSupprimerCommande = (id) => {
        dispatch(supprimerCommande(id));
    };

    // Gérer la pagination
    const handlePagePrecedente = () => {
        if (page > 0) {
            dispatch(definirPage(page - 1));
        }
    };

    const handlePageSuivante = () => {
        if (page < totalPages - 1) {
            dispatch(definirPage(page + 1));
        }
    };

    return (
        <div>
            <h1>Liste des Commandes</h1>

            {chargement && <p>Chargement...</p>}
            {erreur && <p>Erreur : {erreur}</p>}

            <div>
                <h2>Créer une nouvelle commande</h2>
                <input
                    type="text"
                    placeholder="Détails de la commande"
                    value={detailsCommande}
                    onChange={(e) => setDetailsCommande(e.target.value)}
                />
                <button onClick={handleCreerCommande}>Créer</button>
            </div>

            <div>
                <h2>Rechercher une commande par ID</h2>
                <input
                    type="text"
                    placeholder="ID de la commande"
                    value={commandeId}
                    onChange={(e) => setCommandeId(e.target.value)}
                />
                <button onClick={handleObtenirCommandeParId}>Rechercher</button>
            </div>

            <ul>
                {commandes.map((commande) => (
                    <li key={commande.id}>
                        <p>Commande ID : {commande.id}</p>
                        <p>Détails : {commande.detailsCommande}</p>
                        <p>Statut : {commande.statut}</p>
                        <button onClick={() => handleMettreAJourCommande(commande.id)}>Mettre à jour</button>
                        <button onClick={() => handleSupprimerCommande(commande.id)}>Supprimer</button>
                    </li>
                ))}
            </ul>

            <div>
                <button onClick={handlePagePrecedente} disabled={page === 0}>
                    Précédente
                </button>
                <span>Page {page + 1} / {totalPages}</span>
                <button onClick={handlePageSuivante} disabled={page === totalPages - 1}>
                    Suivante
                </button>
            </div>
        </div>
    );
};

export default Commandes;
