import { useState } from 'react'
import './App.css'
import Navbar from './shared/navbar/Navbar';
import { RouterProvider } from 'react-router-dom';
import { checkUser } from './componant/auth/authSlice';
import { useDispatch, useSelector } from 'react-redux';

function App() {
  const dispatch = useDispatch();
    const user = useSelector((state) => state.auth.user);

    useEffect(() => {
        dispatch(checkUser());
    }, [dispatch]);

    // Vérifiez que le user est mis à jour dans le store après chaque dispatch
    console.log("État utilisateur dans App après checkUser:", user);

    return (
        <div>
            <Navbar user={user} />
            <RouterProvider router={router} />
        </div>
    );
}

export default App
