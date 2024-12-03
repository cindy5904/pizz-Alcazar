import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { loginUser } from './authSlice';
import { useNavigate } from 'react-router-dom';
import '../auth/login.css';
import Header from '../../shared/header/Header';
import Navbar from '../../shared/navbar/Navbar';
import Footer from '../../shared/footer/Footer';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const dispatch = useDispatch();
  const loading = useSelector((state) => state.auth.loading);
  const error = useSelector((state) => state.auth.error);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Form submitted:", { email, motDePasse: password });

    // Attendre la réponse du thunk loginUser
    const resultAction = await dispatch(loginUser({ email, motDePasse: password }));

    // Vérifier si l'action a été remplie avec succès
    if (loginUser.fulfilled.match(resultAction)) {
      console.log("Login successful, navigating to home...");
      navigate('/'); // Rediriger vers la page d'accueil après la connexion
    } else {
      console.error("Login failed:", resultAction.error.message);
    }
  };
  
  

  return (
    <>
    <Navbar/>
    
    <div className="loginPage">
  <div className="overlay">
    <div className="login-container">
      <h2 className='h2Login'>Connexion</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit} className="login-form">
        <div className="form-group">
          <label>Email :</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Mot de passe :</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="submit-button" disabled={loading}>
          {loading ? 'Chargement...' : 'Se connecter'}
        </button>
        <button
          type="button"
          className="register-button"
          onClick={() => navigate('/register')}
        >
          Pas encore de compte ?
        </button>
      </form>
    </div>
  </div>
</div>

    
    <Footer/>
    </>
  );
};

export default Login;
