import React from 'react';
import Zoom from 'react-medium-image-zoom';
import 'react-medium-image-zoom/dist/styles.css';
import menu from "../../assets/images/menu.jpg";
import "./carteVuiew.css";

const CarteView = () => {
  return (
    <div className='container-carteView'>
      <Zoom>
        <img className='img-carteView' src={menu} alt="Carte du Menu" />
      </Zoom>
    </div>
  );
};

export default CarteView;