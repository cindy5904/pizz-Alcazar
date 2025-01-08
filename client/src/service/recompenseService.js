import axios from 'axios';


const URL_API = 
 'http://localhost:8080/api/recompenses';



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

const recompenseService = {
    creerRecompense : async (donneesRecompense) => {
        const response = await axiosInstance.post(`${URL_API}`, donneesRecompense);
        return response.data;
      },
      genererRecompensePourUtilisateur : async (utilisateurId) => {
        await axiosInstance.post(`${URL_API}/generer/${utilisateurId}`);
      },
      recupererHistoriqueRecompenses: async (utilisateurId) => {
        const response = await axios.get(`${URL_API}/historique/${utilisateurId}`); 
        return response.data;
    },
}

export default recompenseService;

