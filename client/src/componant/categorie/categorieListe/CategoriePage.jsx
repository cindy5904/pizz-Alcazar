import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchCategories, selectCategories, supprimerCategorie } from '../categorieSlice';
import Header from '../../../shared/header/Header';
import Navbar from '../../../shared/navbar/Navbar';
import "../categorieListe/categoriePage.css";
import { useNavigate } from 'react-router-dom';
import baseTomate from "../../../assets/images/baseTomate.jpg";
import baseCreme from "../../../assets/images/baseCreme.webP";
import aComposer from "../../../assets/images/aComposer.webP";
import boisson from "../../../assets/images/boisson.webP";
import dessert from "../../../assets/images/dessert.webP";
import pizzaChef from "../../../assets/images/pizzaChef.webP";


const CategoriesPage = () => {
    const dispatch = useDispatch();
    const categories = useSelector(selectCategories);
    const loading = useSelector(state => state.categorie.loading);
    const error = useSelector(state => state.categorie.error);
    const navigate = useNavigate();

    useEffect(() => {
        dispatch(fetchCategories());
    }, [dispatch]);

    if (loading) {
        return <div className="loading">Chargement des catégories...</div>;
    }

    if (error) {
        return <div className="error">Erreur : {error}</div>;
    }

    const handleCategoryClick = () => {
        navigate('/produits');
    };

    const handleDelete = (id) => {
        dispatch(supprimerCategorie(id));
    };

    const handleEdit = (categorie) => {
        console.log("Catégorie à modifier :", categorie); 
        navigate('/formCategorie', {
            state: { categorieActuelle: categorie, mode: 'modifier' } 
        });
    };
    const imageMap = {
        'Pizza base tomate': baseTomate,
        'Pizza base crème fraîche': baseCreme,
        'Pizza du Chef': pizzaChef,
        'A composer': aComposer,
        'Dessert': dessert,
        'Boissons' : boisson,
    };


    return (
        <>
        <Header />
            <Navbar />
            <div className="categories-page">
                <h1>Nos Catégories</h1>
                <button onClick={() => navigate('/formCategorie')} className="create-category-button">
                    Créer une catégorie
                </button>
                <ul className="categories-list">
    {categories.map((categorie) => (
        <li key={categorie.id} className="category-item" onClick={handleCategoryClick} style={{ cursor: 'pointer' }}>
             <img src={imageMap[categorie.nom]} alt={categorie.nom} className="category-image" />
            <h2>{categorie.nom}</h2> 
            <button onClick={(e) => { e.stopPropagation(); handleEdit(categorie); }} className="edit-button">Modifier</button>
            <button onClick={(e) => { e.stopPropagation(); handleDelete(categorie.id); }} className="delete-button">Supprimer</button>
        </li>
    ))}
</ul>
            </div>
        </>
    );
};

export default CategoriesPage;
