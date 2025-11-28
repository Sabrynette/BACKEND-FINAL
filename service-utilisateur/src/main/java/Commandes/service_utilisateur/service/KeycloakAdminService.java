package Commandes.service_utilisateur.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class KeycloakAdminService {

    private final String serverUrl = "http://localhost:8080";
    private final String realm = "microservices-realm";
    private final String adminRealm = "master"; // pour admin-cli
    private final String adminClientId = "admin-cli";
    private final String adminUsername = "admin"; // ton admin Keycloak
    private final String adminPassword = "admin";

    private Keycloak getAdminKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl + "/auth")
                .realm(adminRealm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    public void createUser(String username, String email, String password, String roleName) {
        Keycloak keycloak = getAdminKeycloak();

        // Création de l'utilisateur
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        user.setCredentials(Collections.singletonList(cred));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Échec création utilisateur Keycloak: " + response.getStatus());
        }
        // Récupérer l'ID du user créé
        String userId = keycloak.realm(realm)
                .users()
                .search(username)
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Utilisateur créé mais introuvable"))
                .getId();

        // Récupérer le rôle
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();

        // Ajouter le rôle à l'utilisateur
        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));
    }
}
