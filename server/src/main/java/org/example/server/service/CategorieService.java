package org.example.server.service;

import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    public List<Categorie> getAllCategories() {
        return (List<Categorie>) categorieRepository.findAll();
    }


    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
    }


    public Categorie getCategorieByNom(String nom) {
        return categorieRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec le nom : " + nom));
    }

    public Boolean existsByNom(String nom) {
        return categorieRepository.existsByNom(nom);
    }

    public List<Produit> getProduitsByCategorieId(Long categorieId) {
        return categorieRepository.findProduitsByCategorieId(categorieId);
    }

    public Categorie createCategorie(Categorie categorie) {
        if (existsByNom(categorie.getNom())) {
            throw new ResourceNotFoundException("Une catégorie avec ce nom existe déjà !");
        }
        return categorieRepository.save(categorie);
    }

    public Categorie updateCategorie(Long id, Categorie categorieDetails) {
        Categorie categorie = getCategorieById(id);

        categorie.setNom(categorieDetails.getNom());

        return categorieRepository.save(categorie);
    }


    public void deleteCategorie(Long id) {
        Categorie categorie = getCategorieById(id);
        categorieRepository.delete(categorie);
    }


}
