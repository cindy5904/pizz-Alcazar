import React from 'react';
import logo from '../../assets/images/logoPizzAlcazar.png';
import '../header/header.css';

const Header = () => {
  return (
    <header>
      <div className="logo">
        <img src={logo} alt="Logo Pizzeria" />
      </div>
      <div className="header-info">
        <p>📞 01 71 40 68 00</p>
        <p>🕒 Ouvert : 11h - 23h</p>
      </div>
      <div className="header-actions">
        <button>Commander en ligne</button>
      </div>
    </header>
  );
};

export default Header;
