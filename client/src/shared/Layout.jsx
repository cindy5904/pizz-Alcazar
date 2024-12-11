import React, { useEffect } from "react";
import Navbar from "./navbar/Navbar";
import Footer from "./footer/Footer";
import { Outlet } from "react-router-dom";
import { checkUser } from '../componant/auth/authSlice';
import { useDispatch, useSelector } from "react-redux";

const Layout = () => {
    const user = useSelector((state) => state.auth.user);
    const dispatch = useDispatch();
    useEffect(() => {
        console.log("useEffect de App.js exécuté");
        dispatch(checkUser());
    }, [dispatch]);

    useEffect(() => {
        console.log("État utilisateur dans App après checkUser:", user);
    }, [user]);
    return (
      <div className="body-container">
        <Navbar user={user}/>
        <div className="main-content">
          <Outlet /> {/* Contenu des routes enfant sera rendu ici */}
        </div>
        <Footer />
      </div>
    );
  };

export default Layout;
