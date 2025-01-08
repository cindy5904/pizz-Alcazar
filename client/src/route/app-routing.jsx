import { createBrowserRouter } from "react-router-dom";
import Home from '../views/Home';
import CarteView from "../views/carte/CarteView";
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
import { PayPalScriptProvider } from "@paypal/react-paypal-js";
import Success from "../componant/paiement/success/Success";
import Layout from "../shared/Layout";
import DashboardAdmin from "../componant/utilisateur/admin/DashboardAdmin";
import ProtectedRoute from "../componant/route/ProtectedRoute";
import NotificationCommande from "../componant/utilisateur/admin/NotificationCommande";


const initialPayPalOptions = {
    "client-id": import.meta.env.VITE_PAYPAL_CLIENT_ID,
    currency: "EUR",
};

const router = createBrowserRouter([
    {
        path: "/",
        element: <Layout />, 
        children: [
    {
        path :"/",
        element : <Home />,
    },
    {
        path : "/consulter-la-carte",
        element : <CarteView />,
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
        path: "/produits",
        element: <ProduitListe />, 
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
        element: (
            <PayPalScriptProvider options={initialPayPalOptions}>
                <Paiement />
            </PayPalScriptProvider>
        ),
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
    {
        path: "/success", 
        element: <Success />,
    },
    
    {
        path: "", 
        element: <ProtectedRoute allowedRoles={['ROLE_ADMIN']} />,
        children: [
          { path: "/produits/ajouter", element: <ProduitForm /> },
          { path: "/produits/modifier/:id", element: <ProduitForm /> },
          { path: "/formcategorie", element: <CategorieForm /> },
          { path: "/admin", element: <DashboardAdmin /> },
          { path : "commande-recue", element : <NotificationCommande/>},
        ],
      },



        ]}    
    
    

        
    
    
])

export default router