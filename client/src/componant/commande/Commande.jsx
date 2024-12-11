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
import Header from '../../shared/header/Header';
import Navbar from '../../shared/navbar/Navbar';
import "../commande/commande.css"

const Commandes = () => {
    const dispatch = useDispatch();
    const utilisateur = useSelector((state) => state.auth.user);
    const panier = useSelector((state) => state.panier.panier);
    const { commandes, chargement, erreur, page, totalPages } = useSelector((state) => state.commandes);
    const [commandeId, setCommandeId] = useState('');
    const [detailsCommande, setDetailsCommande] = useState('');

    useEffect(() => {
        if (utilisateur && utilisateur.id) {
            dispatch(obtenirToutesCommandes({ userId: utilisateur.id, page }));
        } else {
            console.error("Utilisateur non connecté. Impossible de récupérer les commandes.");
        }
    }, [dispatch, utilisateur, page]);

    const handleObtenirCommandeParId = () => {
        if (commandeId) {
            dispatch(obtenirCommandeParId(commandeId));
        }
    };

    const handleCreerCommande = () => {
        if (!utilisateur || !utilisateur.id || !panier || !panier.id) {
            console.error("Utilisateur ou panier invalide. Impossible de créer une commande.");
            return;
        }

        const nouvelleCommande = {
            detailsCommande,
            statut: 'EN_COURS',
            userId: utilisateur.id,
            panierId: panier.id,
        };

        dispatch(creerCommande(nouvelleCommande));
        setDetailsCommande('');
    };

    const handleMettreAJourCommande = (id) => {
        const donneesMiseAJour = {
            detailsCommande: 'Commande mise à jour',
            statut: 'LIVREE',
        };
        dispatch(mettreAJourCommande({ id, donneesMiseAJour }));
    };

    const handleSupprimerCommande = (id) => {
        dispatch(supprimerCommande(id));
    };

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
        <>
        <div className="commande-container">
        <h1>Liste des Commandes</h1>

        {chargement && <p>Chargement...</p>}
        {erreur && <p style={{ color: 'red' }}>Erreur : {erreur}</p>}

        {commandes.length > 0 ? (
          <>
            <table className="commande-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Numéro de Commande</th>
                  <th>Statut</th>
                  <th>Produits</th>
                </tr>
              </thead>
              <tbody>
                {commandes.map((commande) => (
                  <tr key={commande.id}>
                    <td>{commande.id}</td>
                    <td>{commande.numeroCommande}</td>
                    <td>{commande.statut}</td>
                    <td>
                      <ul>
                        {commande.itemsCommande.map((item) => (
                          <li key={item.id}>
                            {item.produitNom} (x{item.quantite}) - {item.produitPrix}€
                          </li>
                        ))}
                      </ul>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="pagination-buttons">
              <button onClick={handlePagePrecedente} disabled={page === 0}>
                Page Précédente
              </button>
              <p>
                Page {page + 1} sur {totalPages}
              </p>
              <button onClick={handlePageSuivante} disabled={page >= totalPages - 1}>
                Page Suivante
              </button>
            </div>
          </>
        ) : (
          <p>Aucune commande trouvée.</p>
        )}
      </div>
        </>
    );
};

export default Commandes;
