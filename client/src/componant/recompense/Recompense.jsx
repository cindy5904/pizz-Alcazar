import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { recupererHistoriqueRecompensesThunk } from '../recompense/recompenseSlice';

const HistoriqueRecompenses = ({ utilisateurId }) => {
  const dispatch = useDispatch();
  const { recompenses, chargement, erreur } = useSelector((state) => state.recompense);

  useEffect(() => {
    dispatch(recupererHistoriqueRecompensesThunk(utilisateurId));
  }, [dispatch, utilisateurId]);

  if (chargement) return <p>Chargement des récompenses...</p>;
  if (erreur) return <p>Erreur : {erreur}</p>;

  return (
    <div>
      <h2>Historique des Récompenses</h2>
      {recompenses.length > 0 ? (
        <ul>
          {recompenses.map((recompense) => (
            <li key={recompense.id}>
              {recompense.nom} - {recompense.description} - Code : {recompense.codeRemise}
            </li>
          ))}
        </ul>
      ) : (
        <p>Aucune récompense disponible.</p>
      )}
    </div>
  );
};

export default HistoriqueRecompenses;
