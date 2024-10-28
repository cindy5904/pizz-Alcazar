// produitService.js

import axios from 'axios';

const API_URL = 'http://localhost:8080/api/produits'; // URL de l'API backend

const creerProduit = async (produitData, imageFile) => {
    const formData = new FormData();
    
    // Ajouter les données du produit
    formData.append('nom', produitData.nom);
    formData.append('description', produitData.description);
    formData.append('prix', produitData.prix);
    formData.append('disponibilite', produitData.disponibilite);
    formData.append('categorieId', produitData.categorieId);
    
    // Ajouter le fichier image
    if (imageFile) {
        formData.append('imageFile', imageFile); // 'imageFile' doit correspondre à l'argument dans le backend
    }

    const response = await axios.post(API_URL, formData, {
        headers: {
            'Content-Type': 'multipart/form-data', // Indiquer que nous envoyons des fichiers
        },
    });
    return response.data;
};


// Fonction pour mettre à jour un produit
const mettreAJourProduit = async (produitId, produitData, imageFile) => {
    const formData = new FormData();
    
    // Ajouter les données du produit
    formData.append('nom', produitData.nom);
    formData.append('description', produitData.description);
    formData.append('prix', produitData.prix);
    formData.append('disponibilite', produitData.disponibilite);
    formData.append('categorieId', produitData.categorieId);
    
    // Ajouter le fichier image
    if (imageFile) {
        formData.append('imageFile', imageFile); // 'imageFile' doit correspondre à l'argument dans le backend
    }

    const response = await axios.put(`${API_URL}/${produitId}`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data', // Indiquer que nous envoyons des fichiers
        },
    });
    return response.data;
};


// Fonction pour récupérer tous les produits disponibles
const obtenirProduitsDisponibles = async () => {
    const response = await axios.get(`${API_URL}/disponibles`);
    return response.data;
};

// Fonction pour récupérer les produits par catégorie
const obtenirProduitsParCategorie = async (categorieId) => {
    const response = await axios.get(`${API_URL}/categorie/${categorieId}`);
    return response.data;
};

const obtenirCategories = async () => {
    const response = await axios.get('/api/categories');
    console.log("Réponse de l'API Categories:", response.data); // Vérifiez ici
    return response.data;
};


// Fonction pour supprimer un produit
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
