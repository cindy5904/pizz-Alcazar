import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import CategorieService from '../../service/categorieService'; 


export const fetchCategories = createAsyncThunk('categories/fetchCategories', async () => {
    const response = await CategorieService.obtenirCategories();
    return response;
});

export const creerCategorie = createAsyncThunk('categories/creerCategorie', async (donneesCategorie) => { 
    const response = await CategorieService.creerCategorie(donneesCategorie);
    return response; 
});


export const mettreAJourCategorie = createAsyncThunk('categories/mettreAJourCategorie', async ({ id, data }) => { // Changement ici
    const response = await CategorieService.mettreAJourCategorie(id, data);
    return response; 
});


export const supprimerCategorie = createAsyncThunk('categories/supprimerCategorie', async (id) => { 
    await CategorieService.supprimerCategorie(id);
    return id; 
});

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
            })
            .addCase(creerCategorie.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(creerCategorie.fulfilled, (state, action) => {
                state.loading = false;
                state.categories.push(action.payload); 
            })
            .addCase(creerCategorie.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            })
            
            .addCase(mettreAJourCategorie.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(mettreAJourCategorie.fulfilled, (state, action) => {
                state.loading = false;
                const index = state.categories.findIndex(cat => cat.id === action.payload.id);
                if (index !== -1) {
                    state.categories[index] = action.payload; 
                }
            })
            .addCase(mettreAJourCategorie.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            })
            
            .addCase(supprimerCategorie.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(supprimerCategorie.fulfilled, (state, action) => {
                state.loading = false;
                state.categories = state.categories.filter(cat => cat.id !== action.payload); 
            })
            .addCase(supprimerCategorie.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
            });
    },
});


// Exporter les actions et le reducer
export default categorieSlice.reducer;
