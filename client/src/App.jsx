import { useEffect, useState } from 'react'
import './App.css'
import Navbar from './shared/navbar/Navbar';
import { RouterProvider } from 'react-router-dom';
import router from './route/app-routing'
import { checkUser } from './componant/auth/authSlice';
import { useDispatch, useSelector } from 'react-redux';

function App() {
  console.log("App.js rendu");
    const dispatch = useDispatch();
    const user = useSelector((state) => state.auth.user);

    useEffect(() => {
        console.log("useEffect de App.js exécuté");
        dispatch(checkUser());
    }, [dispatch]);

    useEffect(() => {
        console.log("État utilisateur dans App après checkUser:", user);
    }, [user]);

    

    return (
      <RouterProvider router={router}> {/* Placez le RouterProvider ici */}
      <Navbar user={user} />
      <button onClick={() => dispatch(checkUser())}>Vérifier l'utilisateur manuellement</button>
  </RouterProvider>
    );
}

export default App
