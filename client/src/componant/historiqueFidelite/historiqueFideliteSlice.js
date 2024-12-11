import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import HistoriqueFideliteService from "../../service/historiqueFideliteService";

// Async Thunks (Actions asynchrones)

// Récupérer les commandes par semaine
export const fetchCommandesParSemaine = createAsyncThunk(
  "historiqueFidelite/fetchCommandesParSemaine",
  async (dateDebut, { rejectWithValue }) => {
    try {
      return await HistoriqueFideliteService.getCommandesParSemaine(dateDebut);
    } catch (error) {
      return rejectWithValue(error.response?.data || "Une erreur s'est produite");
    }
  }
);

// Récupérer les récompenses par semaine
export const fetchRecompensesParSemaine = createAsyncThunk(
  "historiqueFidelite/fetchRecompensesParSemaine",
  async (dateDebut, { rejectWithValue }) => {
    try {
      return await HistoriqueFideliteService.getRecompensesParSemaine(dateDebut);
    } catch (error) {
      return rejectWithValue(error.response?.data || "Une erreur s'est produite");
    }
  }
);

// Comparer les commandes entre deux semaines
export const fetchComparaisonSemaines = createAsyncThunk(
  "historiqueFidelite/fetchComparaisonSemaines",
  async (semaineActuelle, { rejectWithValue }) => {
    try {
      return await HistoriqueFideliteService.compareCommandesEntreSemaines(semaineActuelle);
    } catch (error) {
      return rejectWithValue(error.response?.data || "Une erreur s'est produite");
    }
  }
);

// Slice
const historiqueFideliteSlice = createSlice({
  name: "historiqueFidelite",
  initialState: {
    commandesParSemaine: [],
    recompensesParSemaine: 0,
    comparaisonSemaines: 0,
    loading: false,
    error: null,
  },
  reducers: {}, // Pas de reducers standards ici, on utilise des thunks
  extraReducers: (builder) => {
    builder
      // Gestion de l'état des commandes par semaine
      .addCase(fetchCommandesParSemaine.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchCommandesParSemaine.fulfilled, (state, action) => {
        state.loading = false;
        state.commandesParSemaine = action.payload;
      })
      .addCase(fetchCommandesParSemaine.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Gestion de l'état des récompenses par semaine
      .addCase(fetchRecompensesParSemaine.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchRecompensesParSemaine.fulfilled, (state, action) => {
        state.loading = false;
        state.recompensesParSemaine = action.payload;
      })
      .addCase(fetchRecompensesParSemaine.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Gestion de la comparaison entre semaines
      .addCase(fetchComparaisonSemaines.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchComparaisonSemaines.fulfilled, (state, action) => {
        state.loading = false;
        state.comparaisonSemaines = action.payload;
      })
      .addCase(fetchComparaisonSemaines.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

// Exportez le reducer pour l'ajouter au store
export default historiqueFideliteSlice.reducer;
