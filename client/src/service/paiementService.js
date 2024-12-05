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
    createPayPalOrder: async (commandeId) => {
        const response = await axios.post(
            `${URL_API}/paypal/create-order/${commandeId}`,
            {}, // Corps vide
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            }
        );
        return response.data; // Retourne l'Order ID ou les détails nécessaires
    },

    // Capture du paiement PayPal
    capturePayPalOrder: async (orderId, commandeId) => {
        const response = await axios.post(
            `${URL_API}/paypal/capture-order/${orderId}/${commandeId}`,
            {}, // Corps vide
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            }
        );
        return response.data; // Retourne les détails du paiement capturé
    },
};

export default PaiementService;
