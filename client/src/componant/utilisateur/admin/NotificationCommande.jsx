import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { obtenirCommandesPayees } from '../../paiement/paiementSlice';
import { updateStatutCommande, obtenirCommandesPayeesEtEnCours } from '../../commande/commandeSlice';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import "./notificationCommande.css";


const NotificationCommande = () => {
    const dispatch = useDispatch();

    
    const { commandes, chargement, erreur } = useSelector((state) => state.commandes);


    const statutsPossibles = ['PRETE', 'EN_PREPARATION', 'EN_LIVRAISON', 'TERMINEE', 'EN_COURS', 'ANNULEE'];

    useEffect(() => {
        dispatch(obtenirCommandesPayeesEtEnCours()).then((action) => {
            console.log("Données après dispatch dans NotificationCommande :", action.payload);
        });
    }, [dispatch]);
    

    const handleStatutChange = async (id, nouveauStatut) => {
        try {
            console.log(`Mise à jour du statut de la commande ${id} avec le statut ${nouveauStatut}`);
            const updatedCommande = await dispatch(updateStatutCommande({ id, nouveauStatut })).unwrap();
            console.log("Commande mise à jour avec succès :", updatedCommande);
            if (updatedCommande) {
                toast.success(`Le statut de la commande ${id} a été mis à jour en ${nouveauStatut}.`);
            } else {
                toast.error("Réponse du serveur vide ou invalide.");
            }
        } catch (error) {
            console.error("Erreur lors de la mise à jour du statut :", error);
            toast.error("Impossible de mettre à jour le statut.");
        }
    };

    if (chargement) {
        return <div>Chargement des commandes payées...</div>;
    }

    if (erreur) {
        return <div>Erreur : {erreur}</div>;
    }

    console.log("Commandes Redux avant filtrage :", commandes);

    const commandesFiltrees = commandes.filter((commande) => {
        const isValid =
            commande.statut === "EN_COURS" &&
            commande.paiementId &&
            commande.paiementMontant > 0;
    
        if (!isValid) {
            console.log("Commande ignorée :", commande);
        }
    
        return isValid;
    });
    console.log("Commandes filtrées :", commandesFiltrees);
    

    return (
        <div className="container-notification">
            <h2 className="notification-title">Commandes en cours</h2>
            {commandesFiltrees.length === 0 ? (
                <p className="no-command">Aucune commande payée avec le statut EN_COURS pour le moment.</p>
            ) : (
                <ul className="command-list">
    {commandesFiltrees.map((commande) => (
        <li className="command-item" key={commande.id}>
            <strong className="command-number">Commande #{commande.numeroCommande}</strong>
            <p className="command-details">Détails : {commande.detailsCommande}</p>
            <p className="command-status">Statut : {commande.statut}</p>
            <p className="user-info">
                Adresse : {commande.user?.adresse || "Non renseignée"},<br />
                Téléphone : {commande.user?.telephone || "Non renseigné"},<br />
                Email : {commande.user?.email || "Non renseigné"}
            </p>
            <p className="payment-info">
                Paiement : {commande.paiementId ? `ID ${commande.paiementId}, Montant ${commande.paiementMontant}€` : "Aucun paiement"}
            </p>
            {commande.itemsCommande && (
                <ul>
                    {commande.itemsCommande.map((item) => (
                        <li key={item.id}>
                            Produit : {item.produit}, Quantité : {item.quantite}
                        </li>
                    ))}
                </ul>
            )}
            <label className="status-label">
                Modifier le statut :
                <select
                    className="status-select"
                    defaultValue={commande.statut}
                    onChange={(e) => handleStatutChange(commande.id, e.target.value)}
                >
                    {statutsPossibles.map((statut) => (
                        <option className="status-option" key={statut} value={statut}>
                            {statut}
                        </option>
                    ))}
                </select>
            </label>
        </li>
    ))}
</ul>
        

            )}
        </div>
    );
};

export default NotificationCommande;

