import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { login, register, logout as logoutService } from '../../service/authService';
import axios from 'axios';
import * as jwtDecode from 'jwt-decode';

// Configurer axios pour envoyer le token d'authentification avec chaque requête
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('token'); // Récupérer le token du localStorage
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`; // Ajouter le token dans l'en-tête
    }
    return config;
}, (error) => {
    return Promise.reject(error); // Gérer les erreurs de la requête
});

// Thunk pour l'enregistrement
export const registerUser = createAsyncThunk('auth/register', async (userData) => {
    const response = await register(userData);
    return response; // Renvoie les données de l'utilisateur
});

// Thunk pour la connexion
export const loginUser = createAsyncThunk('auth/login', async (userData) => {
    const response = await login(userData);
    console.log('Login response:', response); // Vérifiez ce qui est renvoyé
    return { token: response }; // Retournez le token
});

// Thunk pour la déconnexion
export const logoutUser = createAsyncThunk('auth/logout', async () => {
    await logoutService(); // Appelle le service de déconnexion
    localStorage.removeItem('token'); 
});

// Nouvelle thunk pour vérifier l'utilisateur
// export const checkUser = createAsyncThunk('auth/checkUser', async () => {
//     const token = localStorage.getItem('token');
//     if (token) {
//         return { token }; // Vous pouvez aussi renvoyer d'autres infos si nécessaire
//     }
//     return null; // Pas de token
// });
export const checkUser = createAsyncThunk('auth/checkUser', async () => {
    const token = localStorage.getItem('token');
    if (token) {
        const decodedToken = jwtDecode(token); // Remplacez jwtDecode par votre fonction de décodage
        console.log(decodedToken);
        return { email: decodedToken.sub }; // Assurez-vous de retourner l'email
    }
    return null;
});

// Création du slice
const authSlice = createSlice({
    name: 'auth',
    initialState: {
        user: null,  // Pour stocker les informations de l'utilisateur
        token: null, // Pour stocker le token JWT
        loading: false,
        error: null,
    },
    reducers: {
        // Action pour réinitialiser l'état lors de la déconnexion
        resetAuthState: (state) => {
            state.user = null;
            state.token = null;
            state.loading = false;
            state.error = null;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(registerUser.pending, (state) => {
                state.loading = true; // Indique que le chargement est en cours
            })
            .addCase(registerUser.fulfilled, (state, action) => {
                state.loading = false;
                state.user = action.payload; // Enregistre les données de l'utilisateur
            })
            .addCase(registerUser.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message; // Gère l'erreur
            })
            .addCase(loginUser.pending, (state) => {
                state.loading = true;
            })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.loading = false;
                const { token } = action.payload;
                state.token = token;
                console.log(token);
                localStorage.setItem('token', token); // Assurez-vous de stocker le token
                state.user = { email: action.meta.arg.email }; // Enregistrez l'utilisateur
            })
            .addCase(loginUser.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message; // Gère l'erreur
            })
            .addCase(logoutUser.fulfilled, (state) => {
                state.user = null; // Réinitialise les données de l'utilisateur
                state.token = null; // Réinitialise le token
            })
            // Nouveau cas pour vérifier l'utilisateur
            .addCase(checkUser.fulfilled, (state, action) => {
                console.log("checkUser fulfilled action payload:", action.payload);
                if (action.payload) {
                    state.user = action.payload; // Mettre à jour user avec l'email
                    console.log("User and token restored in state:", state.user, state.token);
                } else {
                    state.user = null;
                    state.token = null;
                }
            });
    },
});

// Exporte les actions et le reducer
export const { resetAuthState } = authSlice.actions;
export default authSlice.reducer;
