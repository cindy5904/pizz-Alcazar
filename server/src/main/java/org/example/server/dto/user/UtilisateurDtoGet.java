package org.example.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.entity.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDtoGet {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private int pointsFidelite;
    private Role roles;

    public UtilisateurDtoGet(Long id, String nom, String prenom) {
    }
}
