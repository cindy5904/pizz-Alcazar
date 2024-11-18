import axios from 'axios';

const API_URL = 'http://localhost:8080/api/panier';

axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

const PanierService = {
    ajouterOuMettreAJourItem: async (userId, itemData) => {
        const response = await axios.post(`${API_URL}?userId=${userId}`, itemData);
        return response.data;
    },

    
    getPanierByUserId: async (userId) => {
        const response = await axios.get(`${API_URL}/user/${userId}`);
        return response.data;
    },

    
    getProduitsByPanierId: async (panierId) => {
        const response = await axios.get(`${API_URL}/${panierId}/produits`);
        return response.data;
    },

    supprimerPanier: async (panierId) => {
        await axios.delete(`${API_URL}/${panierId}`);
    }
};

export default PanierService;
