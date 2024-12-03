import React from "react";
import { Link } from "react-router-dom";
import {
  FaPhoneAlt,
  FaEnvelope,
  FaMapMarkerAlt,
  FaFacebook,
  FaInstagram,
  FaTwitter,
} from "react-icons/fa";
import "./footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        {/* Section coordonnées */}
        <div className="footer-coordinates">
          <h3>Contactez-nous</h3>
          <ul>
            <li>
              <FaPhoneAlt className="footer-icon" />{" "}
              <a href="tel:+330171406800">+33 01 71 40 68 00</a>
            </li>
            <li>
              <FaEnvelope className="footer-icon" />{" "}
              <a href="mailto:h.bouhsen@gmail.com">h.bouhsen@gmail.com</a>
            </li>
            <li>
              <FaMapMarkerAlt className="footer-icon" /> 27 Rue Jean Jaurès,
              77410 Claye-Souilly, France
            </li>
            <li>
              <a
                href="https://www.google.com/maps/place/27+Rue+Jean+Jaurès,+77410+Claye-Souilly,+France"
                target="_blank"
                rel="noopener noreferrer"
                className="footer-map-link"
              >
                Voir sur la carte
              </a>
            </li>
          </ul>
        </div>

        {/* Section réseaux sociaux */}
        <div className="footer-socials">
          <h3>Suivez-nous</h3>
          <div className="social-icons">
            <a
              href="https://www.facebook.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaFacebook />
            </a>
            <a
              href="https://www.instagram.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaInstagram />
            </a>
            <a
              href="https://www.twitter.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaTwitter />
            </a>
          </div>
        </div>

        {/* Section droits réservés */}
        <div className="footer-bottom">
          <p>
            &copy; {new Date().getFullYear()} Pizz'Alcazar. Tous droits
            réservés.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
