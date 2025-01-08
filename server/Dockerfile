# Étape 1 : Construire l'application avec Maven sans exécuter les tests
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copier le fichier pom.xml et les sources
COPY pom.xml .
COPY src ./src

# Construire le projet en ignorant les tests
RUN mvn clean package -DskipTests

# Étape 2 : Créer l'image finale avec OpenJDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le jar généré depuis l'étape de build
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env

# Exposer le port 8080
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
