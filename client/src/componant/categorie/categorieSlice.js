import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import CategorieService from '../../service/categorieService'; // Chemin à ajuster

// Actions asynchrones pour récupérer les catégories
export const fetchCategories = createAsyncThunk('categories/fetchCategories', async () => {
    const response = await CategorieService.obtenirCategories();
    return response;
});
// Ajoutez ce sélecteur pour récupérer les catégories
export const selectCategories = (state) => state.categorie.categories;


const categorieSlice = createSlice({
    name: 'categories',
    initialState: {
        categories: [],
        loading: false,
        error: null,
    },
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchCategories.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchCategories.fulfilled, (state, action) => {
                state.loading = false;
                state.categories = action.payload;
            })
            .addCase(fetchCategories.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            });
    },
});


// Exporter les actions et le reducer
export default categorieSlice.reducer;
