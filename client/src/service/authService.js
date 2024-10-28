import axios from 'axios';

// Remplacez par l'URL de votre API Spring
const API_URL = 'http://localhost:8080/api/auth'; 

// Fonction pour l'enregistrement
export const register = async (userData) => {
  const response = await axios.post(`${API_URL}/register`, userData);
  return response.data; // On renvoie la réponse du serveur
};

// Fonction pour la connexion
export const login = async (userData) => {
  const response = await axios.post(`${API_URL}/login`, userData);
  return response.data; // Renvoie le token d'authentification
};

// Fonction pour la déconnexion (si vous avez besoin de l'implémenter)
export const logout = async () => {
  await axios.post(`${API_URL}/logout`);
};
