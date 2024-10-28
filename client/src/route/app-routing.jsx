import { createBrowserRouter } from "react-router-dom";
import Home from '../views/Home';
import Login from '../componant/auth/Login';
import Register from "../componant/auth/Register";
import ProduitForm from '../componant/produit/produitFormulaire/ProduitForm';
import ProduitListe from "../componant/produit/produitListe/ProduitListe";

const router = createBrowserRouter([
    {
        path :"/",
        element : <Home />,
    },
    {
        path :"/login",
        element : <Login />,

    },
    {
        path :"/register",
        element : <Register />,

    },
    {
        path: "/produits/ajouter",
        element: <ProduitForm />, // Formulaire pour ajouter un produit
    },
    {
        path: "/produits/modifier/:id",
        element: <ProduitForm />, // Formulaire pour modifier un produit spécifique
    },
    {
        path: "/produits",
        element: <ProduitListe />, 
    }
    
    

        
    
    
])

export default router