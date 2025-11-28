# Étape 1 : Build de l'application avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

# Construire le jar Spring Boot
RUN mvn clean package -DskipTests

# Étape 2 : image finale légère avec JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le jar construit
COPY --from=build /workspace/target/config-server-*.jar app.jar

# Exposer le port configuré dans application.yml
EXPOSE 8888

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
