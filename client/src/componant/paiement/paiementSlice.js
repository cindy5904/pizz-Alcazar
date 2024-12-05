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
export const createPayPalOrder = createAsyncThunk(
    'paiements/createPayPalOrder',
    async (commandeId, { rejectWithValue }) => {
        try {
            const response = await PaiementService.createPayPalOrder(commandeId);
            return response; // Order ID PayPal
        } catch (error) {
            return rejectWithValue(error.response.data);
        }
    }
);

// 2. Capture d'une commande PayPal
export const capturePayPalOrder = createAsyncThunk(
    'paiements/capturePayPalOrder',
    async ({ orderId, commandeId }, { rejectWithValue }) => {
        try {
            const response = await PaiementService.capturePayPalOrder(orderId, commandeId);
            return response; // Détails de la transaction capturée
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
        paypalOrderId: null,
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
            })
            .addCase(createPayPalOrder.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
                state.paypalOrderId = null;
            })
            .addCase(createPayPalOrder.fulfilled, (state, action) => {
                state.chargement = false;
                state.paypalOrderId = action.payload; // Enregistrer l'Order ID
            })
            .addCase(createPayPalOrder.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })

            // Gestion de la capture de commande PayPal
            .addCase(capturePayPalOrder.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
                state.statutPaiement = null;
            })
            .addCase(capturePayPalOrder.fulfilled, (state, action) => {
                state.chargement = false;
                state.paiementActuel = action.payload; // Détails de la transaction PayPal
                state.statutPaiement = action.payload.statut || 'REUSSI';
            })
            .addCase(capturePayPalOrder.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
                state.statutPaiement = 'ECHOUE';
            });

            
    },
});

export default paiementSlice.reducer;
