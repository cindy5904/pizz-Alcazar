import React, { useEffect, useRef, useState } from "react";
import Header from "../shared/header/Header";
import Navbar from "../shared/navbar/Navbar";
import "../views/home.css";
import { Link } from "react-router-dom";
import wave from "../assets/images/wave.svg";
import far1 from "../assets/images/LaMonteiraFar.png";
import far2 from "../assets/images/far2.png";
import far3 from "../assets/images/far3.png";
import cat1 from "../assets/images/homecat1.png";
import cat2 from "../assets/images/homecat2.png";
import cat3 from "../assets/images/homecat3.png";
import cat4 from "../assets/images/homecat4.png";
import accessible from "../assets/images/iconAccessible.png";
import livraison from "../assets/images/iconLivraison1.png";
import wifi from "../assets/images/iconWifi.png";
import emporter from "../assets/images/iconEmporter.png";
import parking from "../assets/images/iconParking.png";
import horaire from "../assets/images/horaire.png";
import Footer from "../shared/footer/Footer";


const Home = () => {
  const containerRef = useRef(null); // Référence pour observer la section
  const fidelityTextRef = useRef(null);
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("animate");
          } else {
            entry.target.classList.remove("animate"); // Pour rejouer l'animation
          }
        });
      },
      { threshold: 0.1 } // Déclenche lorsque 10% de l'élément est visible
    );

    // Observer le conteneur d'images
    if (containerRef.current) {
      observer.observe(containerRef.current);
    }

    // Observer le texte de fidélité
    if (fidelityTextRef.current) {
      observer.observe(fidelityTextRef.current);
    }

    // Cleanup
    return () => {
      if (containerRef.current) {
        observer.unobserve(containerRef.current);
      }
      if (fidelityTextRef.current) {
        observer.unobserve(fidelityTextRef.current);
      }
    };
  }, []);

  return (
    <>
      <Header />
     
      <div className="produitFar">
        <h1 className="title-far">Nos produits phares</h1>
        <div className="imgFar">
        <img src={far1} alt="" />
        <img src={far2} alt="" />
        <img src={far3} alt="" />
        </div>  
      </div>
      <div className="containerSvg">
  <img src={wave} alt="Wave" className="wave" />
  {/* div bg red */}
  <div className="contentSvg">
  <div className="fidelityText" ref={fidelityTextRef}>
    <h2 className="h2HomeFidelite">Chez Pizz'Alcazar, on récompense la fidélité</h2>
    <p className="pHomeFidelite">1€ = 1 point de fidélité*</p>
    <p>
      Un code de <strong>10%</strong> tous les <strong>100 points</strong> <br />
      <em>*À valoir sur la prochaine commande</em>
    </p>
  </div>
</div>

  <div className="containerImgCategorieHome"  ref={containerRef}>
    <img src={cat1} alt="Catégorie 1" className="categoryImage" />
    <img src={cat2} alt="Catégorie 2" className="categoryImage" />
    <img src={cat3} alt="Catégorie 3" className="categoryImage" />
    <img src={cat4} alt="Catégorie 4" className="categoryImage" />
  </div>
  </div>
  <div className="section-options">
  <div className="contenu-texte">
        <h2>Venez savourer nos pizzas</h2>
        <p>
  Découvrez l'art authentique de la pizza italienne dans une ambiance chaleureuse et conviviale. Savourez nos créations
  sur place, emportez-les pour un repas fait maison, ou laissez-nous vous les livrer pour un confort absolu. Chaque
  bouchée est une invitation au voyage, portée par la passion de nos maîtres pizzaiolos.
</p>

        <button onClick={() => setModalOpen(true)} className="btn-consulter-horaires">
          Consulter nos horaires ici
        </button>
      
      </div>
        {modalOpen && (
          <div className="modal">
            <div className="modal-content">
              <button onClick={() => setModalOpen(false)} className="btn-fermer-modal">
                ×
              </button>
              <img src={horaire} alt="Horaires d'ouverture" />
            </div>
          </div>
        )}
      </div>
  <div className="serviceHome">
  <h2>Nos services</h2>
  <div className="services-container">
    <div className="service-item">
      <img src={accessible} alt="Accessibilité" className="icon" />
      <p>Accessible aux personnes à mobilité réduite</p>
    </div>
    <div className="service-item">
      <img src={livraison} alt="Livraison" className="icon" />
      <p>Livraison</p>
    </div>
    <div className="service-item">
      <img src={parking} alt="Parking" className="icon" />
      <p>Parking en libre-service</p>
    </div>
    <div className="service-item">
      <img src={emporter} alt="À emporter" className="icon" />
      <p>À emporter</p>
    </div>
    <div className="service-item">
      <img src={wifi} alt="Wi-Fi gratuit" className="icon" />
      <p>Wi-Fi gratuit</p>
    </div>
  </div>
</div>







      
   
    </>
  );
};

export default Home;
