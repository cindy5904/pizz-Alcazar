import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useSelector } from 'react-redux';

const ProtectedRoute = ({ allowedRoles }) => {
    const user = useSelector((state) => state.auth.user);
  console.log("User role:", user?.roles);
  console.log("Allowed roles:", allowedRoles);
  console.log("State auth user:", user);
  console.log("Comparison result:", user.roles[0] === allowedRoles[0]);



  if (!user) {
    
    return <Navigate to="/login" />;
  }

  if (!user.roles.some(role => allowedRoles.includes(role))) {
    return <Navigate to="/" />;
  }
  

  return <Outlet />;
};

export default ProtectedRoute;
