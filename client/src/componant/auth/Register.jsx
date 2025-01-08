import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { registerUser } from "./authSlice";
import { useNavigate } from "react-router-dom";
import "../auth/register.css";
import Navbar from "../../shared/navbar/Navbar";
import Footer from "../../shared/footer/Footer";

const Register = () => {
  const [nom, setNom] = useState("");
  const [prenom, setPrenom] = useState("");
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [adresse, setAdresse] = useState("");
  const [telephone, setTelephone] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});


  const dispatch = useDispatch();
  const loading = useSelector((state) => state.auth.loading);
  const error = useSelector((state) => state.auth.error);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const userData = {
      nom,
      prenom,
      email,
      motDePasse,
      adresse,
      telephone,
    };
  
    try {
      await dispatch(registerUser(userData)).unwrap();
      navigate("/login");
    } catch (err) {
      console.error("Erreur lors de l'enregistrement :", err);
    }
  };
  
  

  return (
    <>
      
      <div className="registerPage">
        <div className="overlay">
          <div className="register-container">
            <h2>Inscription</h2>
            {error && <p className="error">{error}</p>}
            <form onSubmit={handleSubmit} className="register-form">
              <div className="form-group">
                <input
                  type="text"
                  placeholder="Nom"
                  value={nom}
                  onChange={(e) => setNom(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <input
                  type="text"
                  placeholder="Prénom"
                  value={prenom}
                  onChange={(e) => setPrenom(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <input
                  type="email"
                  placeholder="Adresse email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <div className={`form-group ${fieldErrors?.motDePasse ? "has-error" : ""}`}>
                {fieldErrors?.motDePasse && <p className="error">{fieldErrors.motDePasse}</p>} 
                <input
                  type="password"
                  placeholder="Mot de passe"
                  value={motDePasse}
                  onChange={(e) => setMotDePasse(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <input
                  type="text"
                  placeholder="Adresse"
                  value={adresse}
                  onChange={(e) => setAdresse(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <input
                  type="text"
                  placeholder="Téléphone"
                  value={telephone}
                  onChange={(e) => setTelephone(e.target.value)}
                  required
                />
              </div>
              <button
                type="submit"
                className="submit-button"
                disabled={loading}
              >
                {loading ? "Chargement..." : "S'inscrire"}
              </button>
            </form>
          </div>
        </div>
      </div>
      
    </>
  );
};

export default Register;
