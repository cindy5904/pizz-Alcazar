import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import recompenseService from '../../service/recompenseService';

// Thunk pour créer une récompense (Admin uniquement)
export const creerRecompenseThunk = createAsyncThunk(
  'recompense/creerRecompense',
  async (donneesRecompense, { rejectWithValue }) => {
    try {
      return await recompenseService.creerRecompense(donneesRecompense);
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Erreur lors de la création de la récompense');
    }
  }
);

// Thunk pour générer une récompense pour un utilisateur spécifique
export const genererRecompenseThunk = createAsyncThunk(
  'recompense/genererRecompense',
  async (utilisateurId, { rejectWithValue }) => {
    try {
      await recompenseService.genererRecompensePourUtilisateur(utilisateurId);
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Erreur lors de la génération de la récompense');
    }
  }
);

// Thunk pour récupérer l'historique des récompenses
export const recupererHistoriqueRecompensesThunk = createAsyncThunk(
  'recompense/recupererHistoriqueRecompenses',
  async (utilisateurId, { rejectWithValue }) => {
    try {
      return await recompenseService.recupererHistoriqueRecompenses(utilisateurId);
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Erreur lors de la récupération des récompenses');
    }
  }
);

const recompenseSlice = createSlice({
  name: 'recompense',
  initialState: {
    recompenses: [],
    chargement: false,
    erreur: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      
      .addCase(recupererHistoriqueRecompensesThunk.pending, (state) => {
        state.chargement = true;
        state.erreur = null;
      })
      .addCase(recupererHistoriqueRecompensesThunk.fulfilled, (state, action) => {
        state.chargement = false;
        state.recompenses = action.payload;
      })
      .addCase(recupererHistoriqueRecompensesThunk.rejected, (state, action) => {
        state.chargement = false;
        state.erreur = action.payload;
      })

      
      .addCase(creerRecompenseThunk.pending, (state) => {
        state.chargement = true;
        state.erreur = null;
      })
      .addCase(creerRecompenseThunk.fulfilled, (state, action) => {
        state.chargement = false;
        state.recompenses.push(action.payload);
      })
      .addCase(creerRecompenseThunk.rejected, (state, action) => {
        state.chargement = false;
        state.erreur = action.payload;
      })

      
      .addCase(genererRecompenseThunk.pending, (state) => {
        state.chargement = true;
        state.erreur = null;
      })
      .addCase(genererRecompenseThunk.fulfilled, (state) => {
        state.chargement = false;
      })
      .addCase(genererRecompenseThunk.rejected, (state, action) => {
        state.chargement = false;
        state.erreur = action.payload;
      });
  },
});

export default recompenseSlice.reducer;
