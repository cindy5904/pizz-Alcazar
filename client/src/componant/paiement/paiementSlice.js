import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import PaiementService from '../../service/paiementService';

export const creerPaiement = createAsyncThunk(
    'paiements/creerPaiement',
    async (donneesPaiement, { rejectWithValue }) => {
        try {
            const response = await PaiementService.creerPaiement(donneesPaiement);
            return response;
        } catch (error) {
            return rejectWithValue(error.response.data);
        }
    }
);

// 2. Obtenir un paiement par ID
export const obtenirPaiementParId = createAsyncThunk(
    'paiements/obtenirPaiementParId',
    async (id, { rejectWithValue }) => {
        try {
            const response = await PaiementService.obtenirPaiementParId(id);
            return response;
        } catch (error) {
            return rejectWithValue(error.response.data);
        }
    }
);

// 3. Obtenir les paiements d'une commande
export const obtenirPaiementsParCommandeId = createAsyncThunk(
    'paiements/obtenirPaiementsParCommandeId',
    async (commandeId, { rejectWithValue }) => {
        try {
            const response = await PaiementService.obtenirPaiementsParCommandeId(commandeId);
            return response;
        } catch (error) {
            return rejectWithValue(error.response.data);
        }
    }
);

const paiementSlice = createSlice({
    name: 'paiements',
    initialState: {
        paiements: [], 
        paiementActuel: null,
        chargement: false, 
        erreur: null, 
        statutPaiement: null, 
    },
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(creerPaiement.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
                state.statutPaiement = null; 
            })
            .addCase(creerPaiement.fulfilled, (state, action) => {
                state.chargement = false;
                state.paiementActuel = action.payload;
                state.statutPaiement = action.payload.statut; 
            })
            .addCase(creerPaiement.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
                state.statutPaiement = 'ECHOUE'; 
            })
            .addCase(obtenirPaiementParId.fulfilled, (state, action) => {
                state.chargement = false;
                state.paiementActuel = action.payload;
            })
            .addCase(obtenirPaiementsParCommandeId.fulfilled, (state, action) => {
                state.chargement = false;
                state.paiements = action.payload;
            });
    },
});

export default paiementSlice.reducer;
