// produitSlice.js

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import produitService from '../../service/produitService';

// Action pour créer un produit
export const creerProduit = createAsyncThunk(
    'produits/creer',
    async ({ produitData, imageFile }, thunkAPI) => {
        try {
            // Appel au service avec les paramètres requis
            return await produitService.creerProduit(produitData, imageFile);
        } catch (error) {
            console.error("Erreur lors de la création du produit:", error);
            return thunkAPI.rejectWithValue(error.response?.data || "Erreur inconnue");
        }
    }
);

// Action pour mettre à jour un produit
export const mettreAJourProduit = createAsyncThunk(
    'produits/mettreAJour',
    async ({ id, produitData, imageFile }, thunkAPI) => {
        try {
            const response = await produitService.mettreAJourProduit(id, produitData, imageFile);
            return response; // Doit retourner le produit mis à jour
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);

// Action pour récupérer les produits disponibles
export const obtenirProduitsDisponibles = createAsyncThunk(
    'produits/obtenirDisponibles',
    async (_, thunkAPI) => {
        try {
            return await produitService.obtenirProduitsDisponibles();
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);

// Action pour récupérer les produits par catégorie
export const obtenirProduitsParCategorie = createAsyncThunk(
    'produits/obtenirParCategorie',
    async (categorieId, thunkAPI) => {
        try {
            return await produitService.obtenirProduitsParCategorie(categorieId);
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);


export const obtenirCategories = createAsyncThunk(
    'produits/obtenirCategories',
    async (_, thunkAPI) => {
        try {
            return await produitService.obtenirCategories();
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);

// produitSlice.js

export const fetchProduitById = createAsyncThunk(
    'produits/obtenirParId',
    async (produitId, thunkAPI) => {
        try {
            const response = await produitService.obtenirProduitParId(produitId); // Crée une fonction dans produitService
            return response;
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);

// Ajoute un case dans extraReducers pour cette nouvelle action



// Action pour supprimer un produit
export const supprimerProduit = createAsyncThunk(
    'produits/supprimer',
    async (produitId, thunkAPI) => {
        try {
            await produitService.supprimerProduit(produitId);
            return produitId; // Retourne l'ID pour la suppression locale
        } catch (error) {
            return thunkAPI.rejectWithValue(error.response.data);
        }
    }
);

const produitSlice = createSlice({
    name: 'produits',
    initialState: {
        items: [],
        categories: [],
        chargement: false,
        erreur: null,
    },
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(creerProduit.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(creerProduit.fulfilled, (state, action) => {
                state.chargement = false;
                state.items.push(action.payload);
            })
            .addCase(creerProduit.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })

            .addCase(mettreAJourProduit.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(mettreAJourProduit.fulfilled, (state, action) => {
                state.chargement = false;
                const index = state.items.findIndex((item) => item.id === action.payload.id);
                if (index !== -1) state.items[index] = action.payload;
            })
            .addCase(mettreAJourProduit.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })

            .addCase(obtenirProduitsDisponibles.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(obtenirProduitsDisponibles.fulfilled, (state, action) => {
                state.chargement = false;
                state.items = action.payload;
            })
            .addCase(obtenirProduitsDisponibles.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })

            .addCase(obtenirProduitsParCategorie.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(obtenirProduitsParCategorie.fulfilled, (state, action) => {
                console.log("Produits récupérés :", action.payload);
                state.chargement = false;
                state.items = action.payload;
            })
            .addCase(obtenirProduitsParCategorie.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })

            .addCase(supprimerProduit.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(supprimerProduit.fulfilled, (state, action) => {
                state.chargement = false;
                state.items = state.items.filter((item) => item.id !== action.payload);
            })
            .addCase(supprimerProduit.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            });
    },
});

export default produitSlice.reducer;
