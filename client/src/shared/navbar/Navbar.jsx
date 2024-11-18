import React, { useState } from 'react';
import '../navbar/navbar.css'; 
import { FaUser, FaSignOutAlt,FaShoppingCart } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { logoutUser } from '../../componant/auth/authSlice';
import Header from '../header/Header';

const Navbar = () => {
  
    const [isOpen, setIsOpen] = useState(false);
    const user = useSelector((state) => state.auth.user);
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
        <div className="container-nav-link">
        <ul className={`nav-links ${isOpen ? 'open' : ''}`}>
          <li><a href="#home">Accueil</a></li>
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
                    <span>{user.email}</span> 
                    <FaUser className="user-icon cart-icon" /> 
                    <FaSignOutAlt className="logout-icon cart-icon" onClick={handleLogout} /> 
                </>
            ) : (
                <a href="/login">Mon compte</a> 
            )}
            <Link to="/panier"><FaShoppingCart className="panier-icon cart-icon" /></Link>
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
