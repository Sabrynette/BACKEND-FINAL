package Commandes.service_utilisateur.config;

import Commandes.service_utilisateur.service.JwtTokenService;
import Commandes.service_utilisateur.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(@Lazy UserService userService, @Lazy JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");

        System.out.println("➡️ [DEBUG] Requête interceptée : " + method + " " + path);
        System.out.println("   → Authorization: " + authHeader);

        // ✅ Bypass complet pour les routes publiques
        boolean isPublicPath =
                path.endsWith("/register") ||
                        path.endsWith("/login") ||
                        path.contains("/api/auth/") ||
                        path.endsWith("/api/users/register") ||
                        path.endsWith("/api/users/login");


        if (isPublicPath) {
            chain.doFilter(request, response); // laisse passer sans JWT
            return;
        }

        // 🔒 Vérifie le token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // ⚠️ 401 si pas de token
            response.getWriter().write("{\"message\":\"Token manquant ou invalide\"}");
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            String username = jwtTokenService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtTokenService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ Authentification réussie pour : " + username);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"message\":\"Token invalide ou expiré\"}");
                    return;
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"Erreur token : " + e.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }

}
