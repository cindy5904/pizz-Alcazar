package org.example.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor

public class LoginResponse {
    private String token;
    private Set<String> roles;
    @JsonProperty("userId")
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private int pointsFidelite;

    public LoginResponse(String token, Set<String> roles, Long userId, String nom, String prenom, String email, String adresse, String telephone, int pointsFidelite) {
        this.token = token;
        this.roles = roles;
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
        this.pointsFidelite = pointsFidelite;
    }

}
