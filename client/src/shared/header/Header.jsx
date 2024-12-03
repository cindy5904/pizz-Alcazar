import React from 'react';
import '../header/header.css';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <header className="hero">
     <div className="overlay"></div> 
      <div className="hero-content">
        <h1 className='h1Header'>Pizz'Alcazar</h1>
        <h2 className='h2Header'>La meilleure pizza italienne</h2>
        <h2 className='h2Header'>Près de chez vous</h2>
        <Link to="/categories" className="cta-button">Commander</Link>
      </div>
    </header>
  );
};

export default Header;
