import axios from 'axios';

const URL_API = 'http://localhost:8080/api/paiements';


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

const PaiementService = {
    creerPaiement: async (donneesPaiement) => {
        const response = await axios.post(URL_API, donneesPaiement);
        return response.data;
    },

    obtenirPaiementParId: async (id) => {
        const response = await axios.get(`${URL_API}/${id}`);
        return response.data;
    },

    obtenirPaiementsParCommandeId: async (commandeId) => {
        const response = await axios.get(`${URL_API}/commande/${commandeId}`);
        return response.data;
    },
};

export default PaiementService;
