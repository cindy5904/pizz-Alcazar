import axios from 'axios';

const URL_API = 'http://localhost:8080/api/commandes';

// Intercepteur pour inclure le token d'authentification
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, (erreur) => Promise.reject(erreur));

const CommandeService = {
    // 1. Créer une commande
    creerCommande: async (donneesCommande) => {
        const reponse = await axios.post(URL_API, donneesCommande);
        return reponse.data;
    },

    // 2. Récupérer une commande par ID
    obtenirCommandeParId: async (id) => {
        const reponse = await axios.get(`${URL_API}/${id}`);
        return reponse.data;
    },

    // 3. Récupérer toutes les commandes (avec pagination)
    obtenirToutesCommandes: async (page = 0, taille = 10) => {
        const reponse = await axios.get(`${URL_API}?page=${page}&size=${taille}`);
        return reponse.data;
    },

    // 4. Mettre à jour une commande
    mettreAJourCommande: async (id, donneesMiseAJour) => {
        const reponse = await axios.put(`${URL_API}/${id}`, donneesMiseAJour);
        return reponse.data;
    },

    // 5. Supprimer une commande
    supprimerCommande: async (id) => {
        await axios.delete(`${URL_API}/${id}`);
    },
};

export default CommandeService;
