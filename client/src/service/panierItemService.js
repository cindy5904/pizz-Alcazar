import axios from 'axios';

const API_URL = 'http://localhost:8080/api/panier/items';

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

const PanierItemService = {
    
    ajouterOuMettreAJourItem: async (panierId, itemData) => {
        const response = await axios.post(`${API_URL}/${panierId}`, itemData);
        return response.data;
    },

    
    reduireQuantiteItem: async (panierId, produitId, quantite) => {
        console.log("Appel API avec panierId :", panierId);
        console.log("produitId :", produitId);
        console.log("quantite :", quantite);
    
        const response = await axios.put(
            `${API_URL}/${panierId}/${produitId}?quantite=${quantite}`
        );
        return response.data;
    },
    

    
    supprimerItem: async (panierId, produitId) => {
        const response = await axios.delete(`${API_URL}/${panierId}/${produitId}`);
        return response.data;
    }
    
};

export default PanierItemService;
