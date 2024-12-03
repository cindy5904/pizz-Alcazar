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
    
    creerCommande: async (donneesCommande) => {
        const reponse = await axios.post(URL_API, donneesCommande);
        return reponse.data;
    },

    // 2. Récupérer une commande par ID
    obtenirCommandeParId: async (id) => {
        const reponse = await axios.get(`${URL_API}/${id}`);
        return reponse.data;
    },

    // 3. Récupérer toutes les commandes (avec pagination et filtrage par utilisateur)
obtenirToutesCommandes: async (userId, page = 0, taille = 10) => {
    console.log("obtenirToutesCommandes est appelée avec userId :", userId, "page :", page, "taille :", taille);
    if (!userId) {
        throw new Error("userId est requis pour récupérer les commandes.");
    }
    console.log("URL de la requête :", `${URL_API}?userId=${userId}&page=${page}&size=${taille}`);
console.log("Headers :", {
    Authorization: `Bearer ${localStorage.getItem('token')}`
});

    const reponse = await axios.get(`${URL_API}?userId=${userId}&page=${page}&size=${taille}`);
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
