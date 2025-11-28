package Commandes.service_utilisateur.service;


import Commandes.service_utilisateur.config.CustomUserDetails;
import Commandes.service_utilisateur.config.UserMapper;
import Commandes.service_utilisateur.dto.UserDTO;
import Commandes.service_utilisateur.entity.User;
import Commandes.service_utilisateur.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j

public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserKafkaProducer kafkaProducer;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       UserKafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.kafkaProducer = kafkaProducer;
    }

    // Création d'utilisateur
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Trim pour éviter espaces invisibles
        String username = userDTO.getUsername().trim();
        String email = userDTO.getEmail().trim();

        if (existsByUsername(username)) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }

        if (existsByEmail(email)) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = userMapper.toEntity(userDTO);
        user.setUsername(username);
        user.setEmail(email);

        // Encode le mot de passe si nécessaire
        if (!userDTO.getPassword().startsWith("$2a$")) { // bcrypt hash
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            user.setPassword(userDTO.getPassword());
        }

        user.setCreatedAt(LocalDateTime.now());

        // Sauvegarde et flush immédiat
        User savedUser = userRepository.save(user);
        userRepository.flush();

        // Envoi Kafka asynchrone
        String userJson = "{\"id\": " + savedUser.getId() + ", \"username\": \"" + savedUser.getUsername() + "\"}";
        CompletableFuture.runAsync(() -> kafkaProducer.sendUserCreatedEvent(userJson));

        return userMapper.toDto(savedUser);
    }

    // Vérifications
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username.trim());
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.trim());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username.trim()).orElse(null);
    }

    // LoadUser pour Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
        return new CustomUserDetails(user);
    }

    // Authentification manuelle
    public UserDTO authenticateUser(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Mot de passe invalide");
        }
        return userMapper.toDto(((CustomUserDetails) userDetails).getUser());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        return userMapper.toDto(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        user.setUsername(userDTO.getUsername().trim());
        user.setEmail(userDTO.getEmail().trim());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setRole(userDTO.getRole());

        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
    }

    // Exception personnalisée
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

}
