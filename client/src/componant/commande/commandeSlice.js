import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import CommandeService from '../../service/commandeService';

export const creerCommande = createAsyncThunk(
    'commandes/creerCommande',
    async (donneesCommande, { rejectWithValue }) => {
        try {
            const reponse = await CommandeService.creerCommande(donneesCommande);
            return reponse;
        } catch (erreur) {
            return rejectWithValue(erreur.response.data);
        }
    }
);


export const obtenirCommandeParId = createAsyncThunk(
    'commandes/obtenirCommandeParId',
    async (id, { rejectWithValue }) => {
        try {
            const reponse = await CommandeService.obtenirCommandeParId(id);
            return reponse;
        } catch (erreur) {
            return rejectWithValue(erreur.response.data);
        }
    }
);


export const obtenirToutesCommandes = createAsyncThunk(
    'commandes/obtenirToutesCommandes',
    async ({ page = 0, taille = 10 }, { rejectWithValue }) => {
        try {
            const reponse = await CommandeService.obtenirToutesCommandes(page, taille);
            return reponse;
        } catch (erreur) {
            return rejectWithValue(erreur.response.data);
        }
    }
);

// 4. Mettre à jour une commande
export const mettreAJourCommande = createAsyncThunk(
    'commandes/mettreAJourCommande',
    async ({ id, donneesMiseAJour }, { rejectWithValue }) => {
        try {
            const reponse = await CommandeService.mettreAJourCommande(id, donneesMiseAJour);
            return reponse;
        } catch (erreur) {
            return rejectWithValue(erreur.response.data);
        }
    }
);

// 5. Supprimer une commande
export const supprimerCommande = createAsyncThunk(
    'commandes/supprimerCommande',
    async (id, { rejectWithValue }) => {
        try {
            await CommandeService.supprimerCommande(id);
            return id;
        } catch (erreur) {
            return rejectWithValue(erreur.response.data);
        }
    }
);

const commandeSlice = createSlice({
    name: 'commandes',
    initialState: {
        commandes: [],
        commandeActuelle: null,
        chargement: false,
        erreur: null,
        page: 0,
        taille: 10,
        totalPages: 0,
    },
    reducers: {
        definirPage: (state, action) => {
            state.page = action.payload;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(creerCommande.pending, (state) => {
                state.chargement = true;
                state.erreur = null;
            })
            .addCase(creerCommande.fulfilled, (state, action) => {
                state.chargement = false;
                state.commandeActuelle = action.payload;
                state.commandes.push(action.payload);
                
            })
            .addCase(creerCommande.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload;
            })
            .addCase(obtenirCommandeParId.pending, (state) => { state.chargement = true; state.erreur = null; })
            .addCase(obtenirCommandeParId.fulfilled, (state, action) => {
                state.chargement = false;
                state.commandeActuelle = action.payload;
                const index = state.commandes.findIndex((c) => c.id === action.payload.id);
                if (index !== -1) {
                    state.commandes[index] = action.payload;
                } else {
                    state.commandes.push(action.payload);
                }
            })
            .addCase(obtenirCommandeParId.rejected, (state, action) => { state.chargement = false; state.erreur = action.payload; })
            .addCase(obtenirToutesCommandes.fulfilled, (state, action) => {
                state.chargement = false;
                state.commandes = action.payload.content;
                state.totalPages = action.payload.totalPages;
            })
            .addCase(mettreAJourCommande.fulfilled, (state, action) => {
                state.chargement = false;
                const index = state.commandes.findIndex((c) => c.id === action.payload.id);
                if (index !== -1) {
                    state.commandes[index] = action.payload;
                }
            })
            .addCase(supprimerCommande.fulfilled, (state, action) => {
                state.commandes = state.commandes.filter((c) => c.id !== action.payload);
            })
            .addMatcher(
                (action) => action.type.endsWith('/pending'),
                (state) => {
                    state.chargement = true;
                    state.erreur = null;
                }
            )
            .addMatcher(
                (action) => action.type.endsWith('/rejected'),
                (state, action) => {
                    state.chargement = false;
                    state.erreur = action.payload;
                }
            );
    },
});

export const { definirPage } = commandeSlice.actions;

export default commandeSlice.reducer;
