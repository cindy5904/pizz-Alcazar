import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { obtenirPaiementParId, creerPaiement, createPayPalOrder, capturePayPalOrder } from "../paiement/paiementSlice";
import { mettreAJourCommande, obtenirCommandeParId } from "../commande/commandeSlice";
import {
  PayPalScriptProvider,
  PayPalButtons,
} from "@paypal/react-paypal-js";

import paypal from "../../assets/images/paypal.png";
import visa from "../../assets/images/visa.png";
import cb from "../../assets/images/cb.png";

const Paiement = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);
  const commande = useSelector((state) => state.commandes?.commandeActuelle || null);
  console.log("Commande actuelle dans Paiement.jsx via useSelector :", commande);
  const stateCommandes = useSelector((state) => state.commandes);
console.log("État global des commandes dans Redux :", stateCommandes);

  const [moyenPaiement, setMoyenPaiement] = useState("carte");
  const initialOptions = {
    "client-id": import.meta.env.VITE_PAYPAL_CLIENT_ID, 
    currency: "EUR",
    components: "buttons",
  };
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

  const handlePaiement = async () => {
    try {
        if (!commande || !commande.id) {
            alert("L'identifiant de la commande est introuvable.");
            console.error("Erreur : L'objet commande ou son ID est invalide :", commande);
            return;
        }

        console.log("Commande ID utilisé pour le paiement :", commande.id);

        if (moyenPaiement === "paypal") {
            await dispatch(createPayPalOrder(commande.id)).unwrap();
        } else if (moyenPaiement === "carte" || moyenPaiement === "visa") {
            const paiementDonnees = {
                montant: montantTotal,
                moyenPaiement,
                commandeId: commande.id,
                statut: "REUSSI",
            };

            console.log("Données envoyées pour le paiement :", paiementDonnees);

            const resultatPaiement = await dispatch(creerPaiement(paiementDonnees)).unwrap();
            console.log("Résultat du paiement classique :", resultatPaiement);

            navigate("/success", {
                state: {
                    montant: resultatPaiement.montant,
                    datePaiement: resultatPaiement.datePaiement,
                    numeroReference: resultatPaiement.id,
                },
            });
        } else {
            alert("Veuillez sélectionner un moyen de paiement valide.");
        }
    } catch (error) {
        console.error("Erreur lors du traitement du paiement :", error);
        alert("Une erreur est survenue lors du paiement.");
    }
};






  if (chargement) {
    return <p>Chargement de la commande...</p>;
  }

  return (
    <>
        <PayPalScriptProvider options={initialOptions}>
            <div className="paiement-container">
                <h1 className="paiement-title">Paiement</h1>
                {commande ? (
                    <div className="paiement-recap">
                        <h2>Récapitulatif de la commande</h2>
                        <ul>
                            {commande.itemsCommande.map((item) => (
                                <li key={item.id}>
                                    {item.produitNom} - {item.quantite} x {item.produitPrix}€
                                </li>
                            ))}
                        </ul>
                        <h3 className="paiement-total">Montant total : {montantTotal}€</h3>
                        <div className="separator"></div>
                        <h2>Choisissez votre moyen de paiement</h2>
                        <div className="paiement-modes">
                            <button
                                className={`paiement-mode ${moyenPaiement === "carte" ? "active" : ""}`}
                                onClick={() => setMoyenPaiement("carte")}
                            >
                                <img src={cb} alt="Carte bancaire" className="paiement-image" />
                                <span>Carte bancaire</span>
                            </button>
                            <button
                                className={`paiement-mode ${moyenPaiement === "visa" ? "active" : ""}`}
                                onClick={() => setMoyenPaiement("visa")}
                            >
                                <img src={visa} alt="Visa" className="paiement-image" />
                                <span>Visa</span>
                            </button>
                            <button
                                className={`paiement-mode ${moyenPaiement === "paypal" ? "active" : ""}`}
                                onClick={() => setMoyenPaiement("paypal")}
                            >
                                <img src={paypal} alt="PayPal" className="paypal" />
                                <span>PayPal</span>
                            </button>
                        </div>

                        {/* Gestion des paiements */}
                        {moyenPaiement === "paypal" ? (
                            <PayPalButtons
                                createOrder={(data, actions) => {
                                    return actions.order.create({
                                        purchase_units: [
                                            {
                                                amount: {
                                                    value: montantTotal.toFixed(2), // Montant total
                                                },
                                            },
                                        ],
                                    });
                                }}
                                onApprove={async (data, actions) => {
                                    try {
                                        console.log("Order ID envoyé au back-end :", data.orderID);

                                        const details = await actions.order.capture();
                                        console.log("Transaction complète :", details);

                                        await dispatch(
                                            capturePayPalOrder({ orderId: data.orderID, commandeId: commande.id })
                                        ).unwrap();

                                        alert(`Paiement réussi, merci ${details.payer.name.given_name}!`);
                                        navigate("/success");
                                    } catch (error) {
                                        console.error("Erreur lors de la capture du paiement :", error);
                                        alert("Une erreur est survenue lors de la capture du paiement.");
                                    }
                                }}
                                onError={(err) => {
                                    console.error("Erreur PayPal :", err);
                                    alert("Une erreur est survenue lors du paiement.");
                                }}
                            />
                        ) : (
                            <button className="paiement-button" onClick={handlePaiement}>
                                Confirmer le paiement
                            </button>
                        )}
                    </div>
                ) : (
                    <p className="paiement-no-order">Aucune commande en cours.</p>
                )}
            </div>
        </PayPalScriptProvider>
    </>
);

};

export default Paiement;
