package org.example.server.service;

import org.example.server.dto.categorie.CategorieDtoGet;
import org.example.server.dto.categorie.CategorieDtoPost;
import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CategorieRepository;
import org.example.server.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private ProduitRepository produitRepository;

    // Mapper de Categorie vers CategorieDtoGet
    public CategorieDtoGet mapToDtoGet(Categorie categorie) {
        CategorieDtoGet dto = new CategorieDtoGet();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        return dto;
    }

    // Mapper de CategorieDtoPost vers Categorie (création)
    private Categorie mapToEntity(CategorieDtoPost dtoPost) {
        Categorie categorie = new Categorie();
        categorie.setNom(dtoPost.getNom());
        return categorie;
    }

    public List<CategorieDtoGet> getAllCategories() {
        return ((List<Categorie>) categorieRepository.findAll())
                .stream()
                .map(this::mapToDtoGet)
                .collect(Collectors.toList());
    }

    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
    }


    public CategorieDtoGet createCategorie(CategorieDtoPost dtoPost) {
        if (categorieRepository.existsByNom(dtoPost.getNom())) {
            throw new ResourceNotFoundException("La catégorie existe déjà.");
        }

        Categorie categorie = new Categorie();
        categorie.setNom(dtoPost.getNom());

        Categorie savedCategorie = categorieRepository.save(categorie);
        return mapToDtoGet(savedCategorie);
    }


    public CategorieDtoGet updateCategorie(Long id, CategorieDtoPost dtoPost) {
        Categorie existingCategorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));

        existingCategorie.setNom(dtoPost.getNom());
        Categorie updatedCategorie = categorieRepository.save(existingCategorie);
        return mapToDtoGet(updatedCategorie);
    }

    public void deleteCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        categorieRepository.delete(categorie);
    }

    public List<Produit> getProduitsByCategorieId(Long categorieId) {
        Categorie categorie = getCategorieById(categorieId); // Obtenez d'abord la catégorie par ID
        return produitRepository.findByCategorie(categorie); // Utilisez l'instance de catégorie pour rechercher les produits
    }

}
