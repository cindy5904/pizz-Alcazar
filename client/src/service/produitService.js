import axios from 'axios';

const API_URL = 'http://localhost:8080/api/produits'; 

export const creerProduit = async (produitData, imageFile) => {
    console.log("ProduitData reçu dans le service :", produitData); 
    const formData = new FormData();
    
    formData.append('nom', produitData.nom);
    formData.append('description', produitData.description);
    formData.append('prix', produitData.prix);
    formData.append('disponibilite', produitData.disponibilite);
    formData.append('categorieId', produitData.categorieId);
    

    if (imageFile) {
        formData.append('image', imageFile); 
    }

    const response = await axios.post(API_URL, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data;
};


const mettreAJourProduit = async (produitId, produitData, imageFile) => {
    const formData = new FormData();
    
    
    formData.append('nom', produitData.nom);
    formData.append('description', produitData.description);
    formData.append('prix', produitData.prix);
    formData.append('disponibilite', produitData.disponibilite);
    formData.append('categorieId', produitData.categorieId);
    
    if (imageFile) {
        formData.append('image', imageFile); 
    }

    const response = await axios.put(`${API_URL}/${produitId}`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data', 
        },
    });
    return response.data;
};


const obtenirProduitsDisponibles = async () => {
    const response = await axios.get(`${API_URL}/disponibles`);
    return response.data;
};


const obtenirProduitsParCategorie = async (categorieId) => {
    const response = await axios.get(`${API_URL}/categorie/${categorieId}`);
    return response.data;
};

const obtenirCategories = async () => {
    const response = await axios.get('/api/categories');
    console.log("Réponse de l'API Categories:", response.data);
    return response.data;
};



const supprimerProduit = async (produitId) => {
    await axios.delete(`${API_URL}/${produitId}`);
};

export default {
    creerProduit,
    mettreAJourProduit,
    obtenirProduitsDisponibles,
    obtenirProduitsParCategorie,
    supprimerProduit,
    obtenirCategories,
};
