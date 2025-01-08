import axios from 'axios';

const URL_API = 'http://localhost:8080/api/historique-fidelite';
const URL_API_COMMANDE = 'http://localhost:8080/api/commandes';

// Configuration de l'intercepteur pour inclure le token JWT dans chaque requête
axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

const HistoriqueFideliteService = {
    getCommandesParSemaine: async (dateDebut) => {
        try {
            const response = await axios.get(`${URL_API_COMMANDE}/par-semaine`, {
                params: { dateDebut },
            });
            return response.data;
        } catch (error) {
            console.error("Erreur lors de l'appel à getCommandesParSemaine:", error.response?.data || error.message);
            throw error;
        }
    },

    
    getRecompensesParSemaine: async (dateDebut) => {
        const response = await axios.get(`${URL_API}/recompenses-par-semaine`, {
            params: { dateDebut },
        });
        return response.data; 
    },

    
    compareCommandesEntreSemaines: async (semaineActuelle) => {
        const response = await axios.get(`${URL_API}/comparaison-commandes`, {
            params: { semaineActuelle },
        });
        console.log("API Response for comparison:", response.data);
        return response.data; 
    },

    
    createHistoriqueFidelite: async (historiqueData) => {
        const response = await axios.post(URL_API, historiqueData);
        return response.data; 
    },

    
    getHistoriqueParUtilisateur: async (userId) => {
        const response = await axios.get(`${URL_API}/user/${userId}`);
        return response.data; 
    },

    
    getHistoriqueParMois: async (annee, mois) => {
        const response = await axios.get(`${URL_API}/mois/${annee}/${mois}`);
        return response.data; 
    },

    
    countRecompensesParMois: async (annee, mois) => {
        const response = await axios.get(`${URL_API}/compte-recompenses/${annee}/${mois}`);
        return response.data; 
    },
};

export default HistoriqueFideliteService;
