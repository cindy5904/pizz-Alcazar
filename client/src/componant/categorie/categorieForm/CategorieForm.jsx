import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { creerCategorie, mettreAJourCategorie } from '../categorieSlice';
import '../categorieForm/categorieForm.css';
import { useLocation } from 'react-router-dom';
import Header from '../../../shared/header/Header';
import Navbar from '../../../shared/navbar/Navbar';

const CategorieForm = ({ categorieActuelle, mode }) => {
    const dispatch = useDispatch();
    const loading = useSelector(state => state.categorie.loading);
    const error = useSelector(state => state.categorie.error);
    const location = useLocation();
    
    const [nom, setNom] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        // Vérifiez si nous sommes en mode de modification
        if (location.state && location.state.mode === 'modifier') {
            const { categorieActuelle } = location.state;
            console.log("Données de la catégorie actuelle : ", categorieActuelle); // Debugging
            setNom(categorieActuelle.nom); // Préremplir le nom
            setDescription(categorieActuelle.description); // Préremplir la description
        }
    }, [location.state]); // Écoutez les changements dans l'état

    const handleSubmit = (e) => {
        e.preventDefault();
        const donneesCategorie = { nom, description };

        if (location.state && location.state.mode === 'modifier') {
            dispatch(mettreAJourCategorie({ id: location.state.categorieActuelle.id, data: donneesCategorie }));
        } else {
            dispatch(creerCategorie(donneesCategorie));
        }

        // Réinitialiser les champs après l'envoi
        setNom('');
        setDescription('');
    };

    return (
        <>
        <Header/>
        <Navbar/>
        <div>
            <h2>{location.state && location.state.mode === 'modifier' ? 'Modifier la catégorie' : 'Créer une nouvelle catégorie'}</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Nom de la catégorie :</label>
                    <input
                        type="text"
                        value={nom}
                        onChange={(e) => setNom(e.target.value)}
                        required
                    />
                </div>
               
                <button type="submit" disabled={loading}>
                    {loading ? 'Enregistrement...' : (location.state && location.state.mode === 'modifier' ? 'Mettre à jour la catégorie' : 'Créer la catégorie')}
                </button>
                {error && <p className="error">Erreur : {error}</p>}
            </form>
        </div>
        </>
    );
};

export default CategorieForm;
