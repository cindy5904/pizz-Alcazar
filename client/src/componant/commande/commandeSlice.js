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
    async ({ userId, page = 0, taille = 10 }, { rejectWithValue }) => {
        try {
            if (!userId) {
                throw new Error("userId est requis pour récupérer les commandes.");
            }
            const reponse = await CommandeService.obtenirToutesCommandes(userId, page, taille);
            return reponse; 
        } catch (erreur) {
            return rejectWithValue(erreur.response?.data || "Erreur lors de la récupération des commandes.");
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
        
    totalElements: 0, 
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
                console.log("Commande récupérée via Redux obtenir commande par id fulfilled :", action.payload);
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
                console.log("Payload brut dans fulfilled :", action.payload);
            
                state.chargement = false;
            
                
                if (action.payload && action.payload.content) {
                    state.commandes = action.payload.content; 
                    state.page = action.payload.pageable.pageNumber; 
                    state.taille = action.payload.pageable.pageSize; 
                    state.totalPages = action.payload.totalPages; 
                    state.totalElements = action.payload.totalElements || 0; 
                } else {
                    console.error("Payload inattendu :", action.payload);
                    state.commandes = []; 
                    state.totalPages = 0;
                    state.page = 0;
                    state.totalElements = 0;
                }
            
                console.log("State après mise à jour :", state);
            })
            
            
            .addCase(obtenirToutesCommandes.rejected, (state, action) => {
                state.chargement = false;
                state.erreur = action.payload || "Erreur lors de la récupération des commandes.";
                console.error("Erreur lors de la récupération des commandes :", action.payload);
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
