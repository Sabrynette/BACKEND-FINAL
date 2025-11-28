package Commandes.service_utilisateur.controller;

import Commandes.service_utilisateur.dto.AuthResponse;
import Commandes.service_utilisateur.dto.LoginRequest;
import Commandes.service_utilisateur.dto.UserDTO;
import Commandes.service_utilisateur.service.JwtTokenService;
import Commandes.service_utilisateur.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class AuthController {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public AuthController(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        UserDTO userDto = userService.authenticateUser(request.getUsername(), request.getPassword());
        String token = jwtTokenService.generateToken(userService.loadUserByUsername(request.getUsername()));
        return ResponseEntity.ok(new AuthResponse(token, userDto));
    }

}
