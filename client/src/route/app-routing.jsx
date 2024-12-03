import { createBrowserRouter } from "react-router-dom";
import Home from '../views/Home';
import Login from '../componant/auth/Login';
import Register from "../componant/auth/Register";
import ProduitForm from '../componant/produit/produitFormulaire/ProduitForm';
import ProduitListe from "../componant/produit/produitListe/ProduitListe";
import CategoriesPage from "../componant/categorie/categorieListe/CategoriePage";
import CategorieForm from "../componant/categorie/categorieForm/CategorieForm";
import Panier from "../componant/panier/Panier";
import Paiement from "../componant/paiement/Paiement";
import Commandes from "../componant/commande/Commande";
import PagePaiement from "../componant/paiement/PagePaiement";
import ClientDashboard from "../componant/utilisateur/client/ClientDashboard";
import InfoModifClient from "../componant/utilisateur/client/edit/InfoModifClient";

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
        element: <ProduitForm />, 
    },
    {
        path: "/produits/modifier/:id", 
        element: <ProduitForm />, 
    },
    {
        path: "/produits/modifier/:id",
        element: <ProduitForm />, 
    },
    {
        path: "/produits",
        element: <ProduitListe />, 
    },
    {
        path: "/formcategorie",
        element: <CategorieForm />, 
    },
    {
        path: "/categories",
        element: <CategoriesPage />, 
    },

    {
        path: "/panier",
        element: <Panier />, 
    }, 
    {
        path: "/paiement",
        element: <Paiement />, 
    }, 
    {
        path: "/commande",
        element: <Commandes />, 
    }, 
    {
        path: "/page-paiement",
        element: <PagePaiement />, 
    },
    {
        path: "/mon-compte",
        element: <ClientDashboard />, 
    },
    {
        path: "/mon-compte/modifier", 
        element: <InfoModifClient />,
    },



    
    
    

        
    
    
])

export default router