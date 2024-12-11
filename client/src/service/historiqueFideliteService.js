import axios from 'axios';

const URL_API = 'http://localhost:8080/api/historique-fidelite';

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
    // Récupérer le nombre de commandes par semaine
    getCommandesParSemaine: async (dateDebut) => {
        const response = await axios.get(`${URL_API}/commandes-par-semaine`, {
            params: { dateDebut },
        });
        return response.data; // Retourne la liste des commandes par jour
    },

    // Récupérer le nombre de récompenses par semaine
    getRecompensesParSemaine: async (dateDebut) => {
        const response = await axios.get(`${URL_API}/recompenses-par-semaine`, {
            params: { dateDebut },
        });
        return response.data; // Retourne le total des récompenses
    },

    // Comparer les commandes entre deux semaines
    compareCommandesEntreSemaines: async (semaineActuelle) => {
        const response = await axios.get(`${URL_API}/comparaison-commandes`, {
            params: { semaineActuelle },
        });
        return response.data; // Retourne le pourcentage d'augmentation ou de diminution
    },

    // Créer un historique de fidélité
    createHistoriqueFidelite: async (historiqueData) => {
        const response = await axios.post(URL_API, historiqueData);
        return response.data; // Retourne l'objet HistoriqueFidelite créé
    },

    // Récupérer l'historique par utilisateur
    getHistoriqueParUtilisateur: async (userId) => {
        const response = await axios.get(`${URL_API}/user/${userId}`);
        return response.data; // Retourne une liste des historiques pour cet utilisateur
    },

    // Récupérer l'historique par mois
    getHistoriqueParMois: async (annee, mois) => {
        const response = await axios.get(`${URL_API}/mois/${annee}/${mois}`);
        return response.data; // Retourne une liste des historiques pour le mois spécifié
    },

    // Compter les récompenses par mois
    countRecompensesParMois: async (annee, mois) => {
        const response = await axios.get(`${URL_API}/compte-recompenses/${annee}/${mois}`);
        return response.data; // Retourne le total des récompenses pour le mois spécifié
    },
};

export default HistoriqueFideliteService;
