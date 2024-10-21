-- Supprimer les anciens rôles pour éviter les conflits
DELETE FROM roles WHERE name IN ('ROLE_ADMIN', 'ROLE_CLIENT');

-- Insérer les nouveaux rôles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_CLIENT');

-- Insérer l'utilisateur admin si ce n'est pas déjà fait
INSERT INTO users (nom, prenom, email, mot_de_passe)
VALUES ('Gérant', 'Pizzeria', 'admin@pizzeria.com', '$2a$10$encodedPassword')
    ON DUPLICATE KEY UPDATE email = 'admin@pizzeria.com';

-- Associer le rôle admin à l'utilisateur
INSERT INTO users_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'admin@pizzeria.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
       )
    ON DUPLICATE KEY UPDATE user_id = user_id;
