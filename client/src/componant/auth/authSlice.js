import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  login,
  register,
  logout as logoutService,
updateUser} from "../../service/authService";
import axios from "axios";
import * as jwtDecode from "jwt-decode";

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const registerUser = createAsyncThunk(
  "auth/register",
  async (userData) => {
    const response = await register(userData);
    return response;
  }
);

export const loginUser = createAsyncThunk("auth/login", async (userData) => {
  const response = await login(userData);
  console.log("Login response export loginUser:", response);
  return {
    token: response.token,
    roles: response.roles,
    id: response.userId,
  };
});

export const logoutUser = createAsyncThunk("auth/logout", async () => {
  await logoutService();
  localStorage.removeItem("token");
});

export const checkUser = createAsyncThunk("auth/checkUser", async () => {
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const user = JSON.parse(localStorage.getItem("user"));
  

      console.log(
        "Récupération des détails de l'utilisateur depuis le backend..."
      );
      const response = await axios.get(
        "http://localhost:8080/api/auth/user/details",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const userDetails = {
        id: response.data.id,
        email: response.data.email,
        roles: response.data.roles,
        nom: response.data.nom,
        prenom: response.data.prenom,
        adresse: response.data.adresse,
        telephone: response.data.telephone,
        pointsFidelite: response.data.pointsFidelite,
      };

      localStorage.setItem("user", JSON.stringify(userDetails));
      return { ...userDetails, token };
    } catch (error) {
      console.error(
        "Erreur lors de la récupération des détails de l'utilisateur :",
        error
      );
      localStorage.removeItem("token");
      return null;
    }
  }
  return null;
});

export const updateUserProfile = createAsyncThunk(
    "auth/updateUserProfile",
    async ({ userId, updatedData }, { rejectWithValue }) => {
      console.log("updateUserProfile appelé avec :", { userId, updatedData });
      try {
        const response = await updateUser(userId, updatedData);
        console.log("Réponse API reçue :", response);
        return response;
      } catch (error) {
        console.error("Erreur dans updateUserProfile :", error.response || error.message);
        return rejectWithValue(error.response?.data || "Erreur lors de la mise à jour");
      }
    }
  );
  
  

const authSlice = createSlice({
  name: "auth",
  initialState: {
    user: null,
    token: localStorage.getItem("token") || null,
    loading: false,
    error: null,
  },
  reducers: {
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
        state.loading = true;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        console.log("Payload reçu dans loginUser.fulfilled :", action.payload);

        const {
          token,
          roles,
          id,
          nom,
          prenom,
          email,
          adresse,
          telephone,
          pointsFidelite,
        } = action.payload;

        if (!nom || !prenom || !email) {
          console.error(
            "Des champs sont manquants dans la réponse :",
            action.payload
          );
        }

        state.token = token;
        state.user = {
          id,
          email,
          roles,
          nom,
          prenom,
          adresse,
          telephone,
          pointsFidelite,
        };

        localStorage.setItem("token", token);
        localStorage.setItem(
          "user",
          JSON.stringify({
            id,
            email,
            roles,
            nom,
            prenom,
            adresse,
            telephone,
            pointsFidelite,
          })
        );

        console.log(
          "Vérification de localStorage après connexion :",
          JSON.parse(localStorage.getItem("user"))
        );
      })

      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.token = null;
      })

      .addCase(checkUser.fulfilled, (state, action) => {
        console.log("Payload reçu dans checkUser.fulfilled :", action.payload);
        if (action.payload) {
          state.token = action.payload.token;
          state.user = {
            id: action.payload.id,
            email: action.payload.email,
            roles: action.payload.roles,
            nom: action.payload.nom,
            prenom: action.payload.prenom,
            adresse: action.payload.adresse,
            telephone: action.payload.telephone,
            pointsFidelite: action.payload.pointsFidelite,
          };
        } else {
          state.user = null;
          state.token = null;
        }
      })
      .addCase(updateUserProfile.pending, (state) => {
        state.loading = true;
      })
      .addCase(updateUserProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.user = { ...state.user, ...action.payload };
        console.log("Utilisateur mis à jour dans le state :", state.user);
      })
      .addCase(updateUserProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Erreur lors de la mise à jour.";
      });
  },
});

export const { resetAuthState } = authSlice.actions;
export default authSlice.reducer;
