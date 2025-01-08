import axios from 'axios';

const URL_API = 'http://localhost:8080/api/commandes';

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

    obtenirCommandeParId: async (id) => {
        const reponse = await axios.get(`${URL_API}/${id}`);
        return reponse.data;
    },
    updateStatutCommande: async (id, nouveauStatut) => {
        const response = await axios.put(`${URL_API}/${id}/statut`, { statut: nouveauStatut });
        console.log("Réponse backend :", response); 
        return response.data;
    },

   
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

    mettreAJourCommande: async (id, donneesMiseAJour) => {
        const reponse = await axios.put(`${URL_API}/${id}`, donneesMiseAJour);
        return reponse.data;
    },

    
    supprimerCommande: async (id) => {
        await axios.delete(`${URL_API}/${id}`);
    },
    
    obtenirCommandesPayeesEtEnCours: async () => {
        const reponse = await axios.get(`${URL_API}/commandes-payees-et-en-cours`);
        return reponse.data;
    },
};

export default CommandeService;
