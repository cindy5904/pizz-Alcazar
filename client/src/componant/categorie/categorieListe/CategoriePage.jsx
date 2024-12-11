import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchCategories,
  selectCategories,
  supprimerCategorie,
} from "../categorieSlice";
import Navbar from "../../../shared/navbar/Navbar";
import "../categorieListe/categoriePage.css";
import { useNavigate } from "react-router-dom";
import cat1 from "../../../assets/images/cat1.png";
import cat2 from "../../../assets/images/cat2.png";
import cat3 from "../../../assets/images/cat3.png";
import cat4 from "../../../assets/images/cat4.png";
import cat5 from "../../../assets/images/cat5.png";
import cat6 from "../../../assets/images/cat6.png";
import homme from "../../../assets/images/hommeTenantPizza.png"
import Footer from "../../../shared/footer/Footer";

const CategoriesPage = () => {
  const dispatch = useDispatch();
  const categories = useSelector(selectCategories);
  const loading = useSelector((state) => state.categorie.loading);
  const error = useSelector((state) => state.categorie.error);
  const navigate = useNavigate();
  const roles = useSelector((state) => state.auth.user?.roles || []);
console.log("Rôles de l'utilisateur dans CategoriePage :", roles);


  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  if (loading) {
    return <div className="loading">Chargement des catégories...</div>;
  }

  if (error) {
    return <div className="error">Erreur : {error}</div>;
  }

  const handleCategoryClick = (categorieId) => {
    console.log("ID de la catégorie cliquée :", categorieId);
    navigate("/produits", { state: { categorieId } }); // Transmettre l'ID via le state
};



  const handleDelete = (id) => {
    dispatch(supprimerCategorie(id));
  };

  const handleEdit = (categorie) => {
    console.log("Catégorie à modifier :", categorie);
    navigate("/formCategorie", {
      state: { categorieActuelle: categorie, mode: "modifier" },
    });
  };
  const imageMap = {
    "Pizza base tomate": cat6,
    "Pizza base crème fraîche": cat5,
    "Pizza du Chef": cat2,
    "A composer": cat1,
    Dessert: cat3,
    Boissons: cat4,
  };

  return (
    <>
    
      <div className="categories-page">
  <div className="categories-banner">
    <h1>Découvrez une sélection pour tous les goûts et toutes les envies !</h1>
    <img
      src={homme} 
      alt="Bienvenue"
      className="banner-image"
    />
  </div>
  {Array.isArray(roles) && roles.includes("ROLE_ADMIN") && (
    <button
      onClick={() => navigate("/formCategorie")}
      className="create-category-button"
    >
      Créer une catégorie
    </button>
  )}
  <ul className="categories-list">
    {categories.map((categorie) => (
      <li
        key={categorie.id}
        className="category-item"
        onClick={() => handleCategoryClick(categorie.id)}
      >
        <img
          src={imageMap[categorie.nom]}
          alt={categorie.nom}
          className="category-image"
        />
        <div className="category-details">
          
          {Array.isArray(roles) && roles.includes("ROLE_ADMIN") && (
            <div className="buttons-container">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleEdit(categorie);
                }}
                className="edit-button"
              >
                Modifier
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete(categorie.id);
                }}
                className="delete-button"
              >
                Supprimer
              </button>
            </div>
          )}
        </div>
      </li>
    ))}
  </ul>
</div>

      
    </>
  );
};

export default CategoriesPage;
