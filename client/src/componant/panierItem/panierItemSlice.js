import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import PanierItemService from "../../service/panierItemService";
import { fetchPanierByUserId } from "../panier/panierSlice";

export const ajouterOuMettreAJourItem = createAsyncThunk(
  "panierItem/ajouterOuMettreAJourItem",
  async ({ panierId, itemData }) => {
    const response = await PanierItemService.ajouterOuMettreAJourItem(
      panierId,
      itemData
    );
    return response;
  }
);

export const reduireQuantiteItem = createAsyncThunk(
  "panierItem/reduireQuantiteItem",
  async ({ panierId, produitId, quantite }, { dispatch }) => {
    const response = await PanierItemService.reduireQuantiteItem(
      panierId,
      produitId,
      quantite
    );
    await dispatch(fetchPanierByUserId(panierId));
    return response.data;
  }
);

export const supprimerItem = createAsyncThunk(
  "panierItem/supprimerItem",
  async ({ panierId, produitId }) => {
    await PanierItemService.supprimerItem(panierId, produitId);
    return { panierId, produitId };
  }
);

const panierItemSlice = createSlice({
  name: "panierItem",
  initialState: {
    items: [],
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
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(ajouterOuMettreAJourItem.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(reduireQuantiteItem.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(reduireQuantiteItem.fulfilled, (state, action) => {
        state.loading = false;
        const item = state.items.find((i) => i.id === action.payload.id);
        if (item) {
          item.quantite = action.payload.quantite;
        }
      })
      .addCase(reduireQuantiteItem.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(supprimerItem.fulfilled, (state, action) => {
        state.items = state.items.filter(
          (item) => item.produit.id !== action.payload.produitId
        );
      });
  },
});

export default panierItemSlice.reducer;
