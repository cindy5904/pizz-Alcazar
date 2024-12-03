import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth'; 

export const register = async (userData) => {
  const response = await axios.post(`${API_URL}/register`, userData);
  return response.data; 
};


export const login = async (userData) => {
  const response = await axios.post(`${API_URL}/login`, userData);
  console.log('Backend response dans le frontend:', response.data); 
  return response.data; 
};


export const logout = async () => {
  await axios.post(`${API_URL}/logout`);
};

export const updateUser = async (userId, updatedData) => {
  try {
    console.log("Tentative d'envoi de la requête PUT : ", {
      endpoint: `${API_URL}/update`,
      payload: updatedData,
    });

    const response = await axios.put(`${API_URL}/update`, updatedData);

    console.log("Réponse reçue : ", response.data);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la mise à jour des informations utilisateur :", error.response || error.message);
    throw error;
  }
};




