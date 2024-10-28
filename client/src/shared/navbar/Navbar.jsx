import React, { useState } from 'react';
import '../navbar/navbar.css'; 
import { FaUser, FaSignOutAlt,FaShoppingCart } from 'react-icons/fa';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';

const Navbar = () => {
  
    const [isOpen, setIsOpen] = useState(false);
    const user = useSelector((state) => state.auth.user);
    console.log('User dans Navbar:', user);
    const handleLogout = () => {
        dispatch(logoutUser()); // Appeler l'action de déconnexion
        localStorage.removeItem('token'); // Supprimer le token du localStorage
    };

  
    const toggleMenu = () => {
      setIsOpen(!isOpen);
    };
  
    return (
      <nav className="navbar">
        <div className="container-nav-link">
        <ul className={`nav-links ${isOpen ? 'open' : ''}`}>
          <li><a href="#home">Accueil</a></li>
          <li><a href="#menu">Menu</a></li>
          <li><a href="#about">About</a></li>
          <li><a href="#contact">Contact</a></li>
          <li><Link to="/produits/ajouter">Ajouter un produit</Link></li>
        </ul>
        </div>
        <div className={`nav-actions ${isOpen ? 'open' : ''}`}>
            {user ? ( // Vérifier si l'utilisateur est connecté
                <>
                    <span>{user.email}</span> {/* Affiche l'email de l'utilisateur */}
                    <FaUser className="user-icon cart-icon" /> {/* Icône utilisateur */}
                    <FaSignOutAlt className="logout-icon cart-icon" onClick={handleLogout} /> {/* Icône de déconnexion */}
                </>
            ) : (
                <a href="/login">Mon compte</a> // Afficher "Mon compte" si non connecté
            )}
            <FaShoppingCart className="panier-icon cart-icon" />
        </div>
        <div className="burger-menu" onClick={toggleMenu}>
          <span></span>
          <span></span>
          <span></span>
        </div>
      </nav>
    );
  }
  
  export default Navbar;
