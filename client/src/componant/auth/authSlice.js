import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { login, register, logout as logoutService } from '../../service/authService';
import axios from 'axios';
import * as jwtDecode from 'jwt-decode';




axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('token'); 
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`; 
    }
    return config;
}, (error) => {
    return Promise.reject(error); 
});


export const registerUser = createAsyncThunk('auth/register', async (userData) => {
    const response = await register(userData);
    return response; 
});


export const loginUser = createAsyncThunk('auth/login', async (userData) => {
    const response = await login(userData);
    console.log('Login response export loginUser:', response); 
    return { 
        token: response.token, 
        roles: response.roles, 
        id: response.userId,
    }; 
});

export const logoutUser = createAsyncThunk('auth/logout', async () => {
    await logoutService();
    localStorage.removeItem('token'); 
});

export const checkUser = createAsyncThunk('auth/checkUser', async () => {
    const token = localStorage.getItem('token');
    if (token) {
        try {
            const user = JSON.parse(localStorage.getItem('user'));
            if (user) {
                return {
                    token,
                    id: user.id, 
                    email: user.email,
                    roles: user.roles
                    
                };
            }

            // Si l'utilisateur n'est pas trouvé dans le localStorage, faites une requête à l'API
            const response = await axios.get('http://localhost:8080/api/auth/user/details', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return {
                token,
                id: response.data.id,
                email: response.data.email,
                roles: response.data.roles
            };
        } catch (error) {
            console.error("Erreur lors de la récupération des détails de l'utilisateur :", error);
            localStorage.removeItem('token');
            return null;
        }
    }
    return null;
});



// Création du slice
const authSlice = createSlice({
    name: 'auth',
    initialState: {
        user: null,  // Pour stocker les informations de l'utilisateur
        token: localStorage.getItem('token') || null,  // Pour stocker le token JWT
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
            // .addCase(loginUser.fulfilled, (state, action) => {
            //     state.loading = false;
            //     const { id, token, roles } = action.payload;
            //     state.token = token;
            //     state.user = { id, email, roles };
            //     localStorage.setItem('token', token);
            //     // state.user = { email: action.meta.arg.email, roles };
            
            //     // Stockez les détails de l'utilisateur dans le localStorage
            //     localStorage.setItem('user', JSON.stringify({ email: action.meta.arg.email, roles }));
            // })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.loading = false;
                const { token, roles, id } = action.payload; // Utilisez `id` ici pour être cohérent avec ce qui est retourné
                state.token = token;
                state.user = { id, email: action.meta.arg.email, roles };
                
                localStorage.setItem('token', token);
                // Stockez les détails de l'utilisateur dans le localStorage
                localStorage.setItem('user', JSON.stringify({ id, email: action.meta.arg.email, roles }));
                console.log('Vérification de localStorage après connexion :', JSON.parse(localStorage.getItem('user')));
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
                console.log("Payload reçu dans checkUser.fulfilled :", action.payload);
                if (action.payload) {
                    state.token = action.payload.token;
                    state.user = {
                        id: action.payload.id,
                        email: action.payload.email,
                        roles: action.payload.roles
                    };
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
