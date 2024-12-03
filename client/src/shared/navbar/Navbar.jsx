import React, { useState } from 'react';
import '../navbar/navbar.css'; 
import { FaUser, FaSignOutAlt,FaShoppingCart } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { logoutUser } from '../../componant/auth/authSlice';
import logo from "../../assets/images/logoPizzAlcazar.png"


const Navbar = () => {
  
    const [isOpen, setIsOpen] = useState(false);
    const user = useSelector((state) => state.auth.user);
    console.log("User dans Navbar après récupération :", user);
    const dispatch = useDispatch();
    console.log('User dans Navbar:', user);

    const handleLogout = () => {
        dispatch(logoutUser());
        localStorage.removeItem('token');
    };

  
    const toggleMenu = () => {
      setIsOpen(!isOpen);
    };
  
    return (
      <>
      <nav className="navbar">
      <div className="logo">
    <Link to="/">
      <img src={logo} alt="Logo Pizz'Alcazar" />
    </Link>
  </div>
      <div className="container-nav-link">
        <ul className={`nav-links ${isOpen ? 'open' : ''}`}>
          <li><Link to="/">Accueil</Link></li>
          <li><a href="#menu">Menu</a></li>
          <li><a href="#about">About</a></li>
          <li><a href="#contact">Contact</a></li>
          <li><Link to="/produits/ajouter">Ajouter un produit</Link></li>
          <li><Link to="/produits">Produit</Link></li>
          <li><Link to="/categories">Cat</Link></li>
        </ul>
      </div>
      <div className={`nav-actions ${isOpen ? 'open' : ''}`}>
        {user ? (
          <>
            <span>Bonjour, {user.prenom}</span>
            <Link to="/mon-compte">
              <FaUser className="user-icon cart-icon" />
            </Link>
            <FaSignOutAlt className="logout-icon cart-icon" onClick={handleLogout} />
          </>
        ) : (
          <Link to="/login">Mon compte</Link>
        )}
        <Link to="/panier">
          <FaShoppingCart className="panier-icon cart-icon" />
        </Link>
      </div>
      <div className="burger-menu" onClick={toggleMenu}>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </nav>
      </>
    );
  }
  
  export default Navbar;
