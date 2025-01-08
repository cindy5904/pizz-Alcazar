import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import PanierService from '../../service/panierService'; 


export const ajouterOuMettreAJourItem = createAsyncThunk(
    'panier/ajouterOuMettreAJourItem',
    async ({ userId, produitId, quantite }) => {
        const itemData = { produitId, quantite };
        const response = await PanierService.ajouterOuMettreAJourItem(userId, itemData);
        return response;
    }
);



export const fetchPanierByUserId = createAsyncThunk('panier/fetchPanierByUserId', async (userId) => {
    const response = await PanierService.getPanierByUserId(userId);
    return response;
});

export const fetchProduitsByPanierId = createAsyncThunk('panier/fetchProduitsByPanierId', async (panierId) => {
    const response = await PanierService.getProduitsByPanierId(panierId);
    return response;
});


export const supprimerPanier = createAsyncThunk('panier/supprimerPanier', async (panierId) => {
    await PanierService.supprimerPanier(panierId);
    return panierId; 
});


export const reinitialiserPanier = createAsyncThunk(
    'panier/reinitialiserPanier',
    async () => {
        return null; 
    }
);
export const selectTotalArticles = (state) => {
    if (state.panier.panier && state.panier.panier.itemsPanier) {
      return state.panier.panier.itemsPanier.reduce((total, item) => total + item.quantite, 0);
    }
    return 0; 
  };
  



const panierSlice = createSlice({
    name: 'panier',
    initialState: {
        panier: null,
        loading: false,
        error: null,
    },
    reducers: {},
    extraReducers: (builder) => {
        builder
        .addCase(ajouterOuMettreAJourItem.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(ajouterOuMettreAJourItem.fulfilled, (state, action) => {
            console.log("Panier mis à jour avec succès :", action.payload);
            state.loading = false;
            state.panier = action.payload; // Met à jour le panier avec les données reçues
        })
        .addCase(ajouterOuMettreAJourItem.rejected, (state, action) => {
            state.loading = false;
            state.error = action.error.message;
        })
            .addCase(fetchPanierByUserId.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchPanierByUserId.fulfilled, (state, action) => {
                console.log("Panier récupéré :", action.payload);
                state.loading = false;
                state.panier = action.payload;
            })
            .addCase(fetchPanierByUserId.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            })
            .addCase(fetchProduitsByPanierId.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchProduitsByPanierId.fulfilled, (state, action) => {
                state.loading = false;
                if (state.panier) {
                    state.panier.itemsPanier = action.payload;
                }
            })
            .addCase(fetchProduitsByPanierId.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            })
            .addCase(supprimerPanier.fulfilled, (state, action) => {
                state.panier = null; 
            })
            .addCase(reinitialiserPanier.fulfilled, (state) => {
                state.panier = null;
            });
    },
});


export default panierSlice.reducer;
