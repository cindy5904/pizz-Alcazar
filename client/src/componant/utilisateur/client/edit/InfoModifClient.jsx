import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { updateUserProfile } from "../../../auth/authSlice";
import { useNavigate } from "react-router-dom";
import "../edit/infoModifClient.css";
import Header from "../../../../shared/header/Header";
import Navbar from "../../../../shared/navbar/Navbar";

const InfoModifClient = () => {
  const user = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    nom: "",
    prenom: "",
    email: "",
    adresse: "",
    telephone: "",
  });

  
  useEffect(() => {
    if (user) {
      setFormData({
        nom: user.nom || "",
        prenom: user.prenom || "",
        email: user.email || "",
        adresse: user.adresse || "",
        telephone: user.telephone || "",
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Formulaire soumis avec les données :", formData);
  
    try {
      const updatedData = await dispatch(
        updateUserProfile({ userId: user.id, updatedData: formData })
      ).unwrap();
      console.log("Données mises à jour :", updatedData);
      navigate("/mon-compte");
    } catch (error) {
      console.error("Erreur lors de la mise à jour :", error);
    }
  };
  
  

  if (!user) {
    return <p>Chargement de vos informations...</p>;
  }
  
  

  return (
    <>
    <Header/>
    <Navbar/>
    <div className="edit-user-info-container">
      <h1>Modifier mes informations</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Nom</label>
          <input
            type="text"
            name="nom"
            value={formData.nom}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Prénom</label>
          <input
            type="text"
            name="prenom"
            value={formData.prenom}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Adresse</label>
          <input
            type="text"
            name="adresse"
            value={formData.adresse}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>Téléphone</label>
          <input
            type="text"
            name="telephone"
            value={formData.telephone}
            onChange={handleChange}
          />
        </div>
        <button type="submit" className="btn btn-primary" onClick={() => console.log("Bouton cliqué")}>
          Enregistrer
        </button>
      </form>
    </div>
    </>
  );
};

export default InfoModifClient;
