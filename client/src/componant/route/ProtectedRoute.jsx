import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useSelector } from "react-redux";

const ProtectedRoute = ({ allowedRoles }) => {
  const user = useSelector((state) => state.auth.user);

  if (user === null || user === undefined) {
    
    return <div>Chargement...</div>;
  }

  if (!user.roles.some((role) => allowedRoles.includes(role))) {
    return <Navigate to="/" />;
  }

  return <Outlet />;
};


export default ProtectedRoute;
