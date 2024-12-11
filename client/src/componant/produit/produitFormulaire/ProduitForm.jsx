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
    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [erreur, setErreur] = useState(null);

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
            console.log("Produit actuel:", produitActuel);
            console.log("Catégorie actuelle:", produitActuel.categorie);
            setProduit({
                nom: produitActuel.nom,
                description: produitActuel.description,
                prix: produitActuel.prix,
                disponibilite: produitActuel.disponibilite,
                categorieId: produitActuel.categorie ? produitActuel.categorie.id : '',
            });
            setImageFile(null);
        }
    }, [produitActuel]);
    

    const handleChange = (e) => {
        const { name, value, type, checked, files } = e.target;
        setProduit({ 
            ...produit, 
            [name]: type === 'checkbox' ? checked : value 
        });

        if (name === 'image') {
            console.log("Fichier d'image sélectionné :", files[0]);
            setImageFile(files[0]);
            const file = files[0];
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
            };
            if (file) {
                reader.readAsDataURL(file);
            } else {
                setImagePreview(null);
            }
        }
        
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErreur(null);
    
        try {
            if (id) {
                const result = await dispatch(mettreAJourProduit({ id, produitData: produit, imageFile }));
                if (result.meta.requestStatus === 'fulfilled') {
                    navigate("/produits");
                } else {
                    setErreur("Erreur lors de la mise à jour du produit.");
                }
            } else {
                const result = await dispatch(creerProduit({ produitData: produit, imageFile }));
                if (result.meta.requestStatus === 'fulfilled') {
                    navigate("/produits");
                } else {
                    setErreur("Erreur lors de la création du produit.");
                }
            }
        } catch (error) {
            console.error("Erreur lors de la soumission :", error);
            setErreur("Une erreur est survenue lors de la soumission.");
        }
    };
    const imageUrl = produitActuel && produitActuel.imagePath
    ? `http://localhost:8080${produitActuel.imagePath}`
    : null;
    

    return (
        <>
            
            <div className="product-form-container">
            <div className="overlay">
                <form className="product-form" onSubmit={handleSubmit}>
                <h2 className='h2ProduitForm'>{id ? 'Modifier le produit' : 'Ajouter un produit'}</h2>
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
                    {/* <div className="form-group">
                        <label>Disponibilité:</label>
                        <input
                            type="checkbox"
                            name="disponibilite"
                            checked={produit.disponibilite}
                            onChange={handleChange}
                        />
                    </div> */}
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
                        <label>Image actuelle:</label>
                        {imageUrl && (
                            <img src={imageUrl} alt="Image du produit" style={{ width: '100px', height: '100px' }} />
                        )}
                    </div>

                    <div className="form-group">
                        <label>Nouvelle Image:</label>
                        <input
                            type="file"
                            name="image"
                            accept="image/*"
                            onChange={handleChange}
                        />
                    </div>
                    <button className='buttonProduitForm' type="submit">{id ? 'Modifier' : 'Ajouter'}</button>
                </form>
                </div>
            </div>
        </>
    );
};

export default ProduitForm;
