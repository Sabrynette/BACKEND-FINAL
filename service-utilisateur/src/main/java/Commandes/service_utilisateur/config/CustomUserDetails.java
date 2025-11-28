package Commandes.service_utilisateur.config;

import Commandes.service_utilisateur.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Compte non expiré
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Compte non verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Identifiants non expirés
    }

    @Override
    public boolean isEnabled() {
        return true; // Compte actif
    }
}
