import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../componant/auth/authSlice';
import produitReducer from '../componant/produit/produitSlice';
import categorieReducer from '../componant/categorie/categorieSlice';
import panierReducer from "../componant/panier/panierSlice";
import panierItemReducer from "../componant/panierItem/panierItemSlice";
import commandeSliceReducer from "../componant/commande/commandeSlice";
import paiementReducer from "../componant/paiement/paiementSlice";
import historiqueFideliteReducer from "../componant/historiqueFidelite/historiqueFideliteSlice";
import recompenseReducer from "../componant/recompense/recompenseSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    produit: produitReducer,
    categorie : categorieReducer,
    panier: panierReducer,
    panierItem: panierItemReducer,
    commandes : commandeSliceReducer,
    paiement : paiementReducer,
    historiqueFidelite: historiqueFideliteReducer,
    recompense: recompenseReducer,
  },
});

export default store;
