// categorieService.js

import axios from 'axios';

const API_URL = 'http://localhost:8080/api/categories'; // URL de l'API backend

// Fonction pour récupérer toutes les catégories

const obtenirCategories = async () => {
    try {
        const response = await axios.get(API_URL);
        console.log("Réponse de l'API Categories:", response.data);
        return response.data;
    } catch (error) {
        console.error("Erreur lors de la récupération des catégories:", error.response?.data || error.message);
        throw error; // Vous pouvez choisir de renvoyer null ou de gérer ça autrement
    }
};



// Fonction pour créer une nouvelle catégorie
const creerCategorie = async (categorieData) => {
    const response = await axios.post(API_URL, categorieData);
    return response.data;
};

// Fonction pour mettre à jour une catégorie
const mettreAJourCategorie = async (categorieId, categorieData) => {
    const response = await axios.put(`${API_URL}/${categorieId}`, categorieData);
    return response.data;
};

// Fonction pour supprimer une catégorie
const supprimerCategorie = async (categorieId) => {
    await axios.delete(`${API_URL}/${categorieId}`);
};

export default {
    obtenirCategories,
    creerCategorie,
    mettreAJourCategorie,
    supprimerCategorie,
};
