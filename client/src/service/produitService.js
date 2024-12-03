import axios from "axios";

const API_URL = "http://localhost:8080/api/produits";
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    console.log("Interceptor : Token utilisé :", token);
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (erreur) => Promise.reject(erreur)
);

const produitService = {
  creerProduit: async (produitData, imageFile) => {
    console.log("ProduitData reçu dans le service :", produitData);
    const formData = new FormData();

    formData.append("nom", produitData.nom);
    formData.append("description", produitData.description);
    formData.append("prix", produitData.prix);
    formData.append("disponibilite", produitData.disponibilite);
    formData.append("categorieId", produitData.categorieId);

    if (imageFile) {
      formData.append("image", imageFile);
    }

    const response = await axios.post(API_URL, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  mettreAJourProduit: async (produitId, produitData, imageFile) => {
    const formData = new FormData();

    formData.append("nom", produitData.nom);
    formData.append("description", produitData.description);
    formData.append("prix", produitData.prix);
    formData.append("disponibilite", produitData.disponibilite);
    formData.append("categorieId", produitData.categorieId);

    if (imageFile) {
      formData.append("image", imageFile);
    }

    const response = await axios.put(`${API_URL}/${produitId}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  obtenirProduitsDisponibles: async () => {
    const response = await axios.get(`${API_URL}/disponibles`);
    return response.data;
  },

  obtenirProduitsParCategorie: async (categorieId) => {
    const response = await axios.get(`${API_URL}/categorie/${categorieId}`);
    return response.data;
  },

  obtenirCategories: async () => {
    const response = await axios.get("/api/categories");
    console.log("Réponse de l'API Categories:", response.data);
    return response.data;
  },

  supprimerProduit: async (produitId) => {
    await axios.delete(`${API_URL}/${produitId}`);
  },
};

export default produitService;
