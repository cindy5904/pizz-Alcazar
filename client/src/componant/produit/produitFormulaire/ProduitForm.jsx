import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { creerProduit, mettreAJourProduit, fetchProduitById, obtenirCategories } from '../produitSlice';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchCategories, selectCategories } from '../../categorie/categorieSlice';
import '../produitFormulaire/produitForm.css';
import Header from '../../../shared/header/Header';
import Navbar from '../../../shared/navbar/Navbar';

const ProduitForm = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [image, setImage] = useState(null);

    const [produit, setProduit] = useState({
        nom: '',
        description: '',
        prix: '',
        disponibilite: true,
        categorieId: '', // Changez 'categorie' en 'categorieId' pour stocker l'ID
    });

    const produitActuel = useSelector((state) =>
        id ? state.produit.items.find((prod) => prod.id === Number(id)) : null
    );

    const categories = useSelector(selectCategories);
    const loadingCategories = useSelector((state) => state.categorie.loading);

    useEffect(() => {
        dispatch(fetchCategories());
    }, [dispatch]);

    useEffect(() => {
        if (id) {
            dispatch(fetchProduitById(id));
        }
    }, [dispatch, id]);

    useEffect(() => {
        if (produitActuel) {
            setProduit({
                nom: produitActuel.nom,
                description: produitActuel.description,
                prix: produitActuel.prix,
                disponibilite: produitActuel.disponibilite,
                categorieId: produitActuel.categorie.id, // Assurez-vous de récupérer l'ID de la catégorie
            });
        }
    }, [produitActuel]);

    const handleChange = (e) => {
        const { name, value, type, checked, files } = e.target;
        setProduit({ 
            ...produit, 
            [name]: type === 'checkbox' ? checked : value 
        });
    
        // Si c'est le champ de fichier, mettez à jour l'état de l'image
        if (name === 'image') {
            setImage(files[0]); // Récupérer le premier fichier sélectionné
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(); // Créer une instance de FormData
    
        // Ajouter le produit sous la clé "produit"
        formData.append('produit', JSON.stringify({
            nom: produit.nom,
            description: produit.description,
            prix: produit.prix,
            disponibilite: produit.disponibilite,
            categorieId: produit.categorieId,
        }));
    
        // Ajouter l'image si elle existe sous la clé "image"
        if (image) {
            formData.append('image', image);
        }
    
        // Vérifier si c'est une mise à jour ou une création
        if (id) {
            dispatch(mettreAJourProduit({ id, formData })); // Envoyer le FormData lors de la mise à jour
        } else {
            dispatch(creerProduit(formData)); // Envoyer le FormData lors de la création
        }
        navigate('/produits');
    };
    

    return (
        <>
            <Header />
            <Navbar />
            <div className="product-form-container">
                <h2>{id ? 'Modifier le produit' : 'Ajouter un produit'}</h2>
                <form className="product-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Nom:</label>
                        <input
                            type="text"
                            name="nom"
                            value={produit.nom}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Description:</label>
                        <textarea
                            name="description"
                            value={produit.description}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Prix:</label>
                        <input
                            type="text"
                            name="prix"
                            value={produit.prix}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Disponibilité:</label>
                        <input
                            type="checkbox"
                            name="disponibilite"
                            checked={produit.disponibilite}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="form-group">
                        <label>Catégorie:</label>
                        <select
                            name="categorieId"
                            value={produit.categorieId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Sélectionnez une catégorie</option>
                            {loadingCategories ? (
                                <option value="">Chargement des catégories...</option>
                            ) : (
                                Array.isArray(categories) && categories.length > 0 ? (
                                    categories.map((cat) => (
                                        <option key={cat.id} value={cat.id}>{cat.nom}</option>
                                    ))
                                ) : (
                                    <option value="">Aucune catégorie disponible</option>
                                )
                            )}
                        </select>
                    </div>
                    <div className="form-group">
    <label>Image:</label>
    <input
        type="file"
        name="image"
        accept="image/*" // Accepte uniquement les fichiers image
        onChange={handleChange}
        required
    />
</div>
                    <button type="submit">{id ? 'Modifier' : 'Ajouter'}</button>
                </form>
            </div>
        </>
    );
};

export default ProduitForm;
