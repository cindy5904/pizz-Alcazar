import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../componant/auth/authSlice';
import produitReducer from '../componant/produit/produitSlice';
import categorieReducer from '../componant/categorie/categorieSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    produit: produitReducer,
    categorie : categorieReducer,
  },
});

export default store;
