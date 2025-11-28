package Commandes.service_utilisateur.controller;

import Commandes.service_utilisateur.config.CustomUserDetails;
import Commandes.service_utilisateur.dto.AuthResponse;
import Commandes.service_utilisateur.dto.LoginRequest;
import Commandes.service_utilisateur.dto.UserDTO;
import Commandes.service_utilisateur.entity.Role;
import Commandes.service_utilisateur.entity.User;
import Commandes.service_utilisateur.service.JwtTokenService;
import Commandes.service_utilisateur.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          JwtTokenService jwtTokenService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            var userDetails = userService.loadUserByUsername(loginRequest.getUsername());
            if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                String token = jwtTokenService.generateToken(userDetails);
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "username", userDetails.getUsername(),
                        "role", ((CustomUserDetails)userDetails).getUser().getRole().name()
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            if (userService.existsByUsername(userDTO.getUsername()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username déjà utilisé"));
            if (userService.existsByEmail(userDTO.getEmail()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email déjà utilisé"));
            if (userDTO.getRole() == null) userDTO.setRole(Role.CLIENT);

            UserDTO createdUser = userService.createUser(userDTO);
            var userDetails = userService.loadUserByUsername(createdUser.getUsername());
            String token = jwtTokenService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", createdUser.getUsername(),
                    "role", createdUser.getRole().name()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur création utilisateur", "error", e.getMessage()));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Tentative d'accès non authentifiée à /me");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        logger.info("Chargement utilisateur connecté : {}", username);
        try {
            User user = userService.findByUsername(username);
            UserDTO userDTO = convertToDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (UserService.ResourceNotFoundException ex) {
            logger.error("Utilisateur non trouvé : {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.debug("Requête pour récupérer tous les utilisateurs");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.info("Mise à jour de l'utilisateur avec l'ID : {}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Suppression de l'utilisateur avec l'ID : {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }





}

