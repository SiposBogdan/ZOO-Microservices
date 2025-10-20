package exemplarservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String username = null;
        List<String> roles = null;

        try {
            username = jwtUtil.extractUsername(token);
            roles = jwtUtil.extractRoles(token); // Extrage rolurile direct din token
        } catch (Exception e) {
            // Loghează eroarea, dar permite cererii să continue.
            // Spring Security va bloca cererea mai târziu dacă un endpoint e @Secured/@PreAuthorize
            // și autentificarea este null.
            logger.warn("JWT Token processing failed for request to " + request.getRequestURI() + ": " + e.getMessage());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Aici, validăm token-ul doar împotriva expirării și semnăturii.
            // Nu mai avem userDetails.loadUserByUsername(username);
            if (jwtUtil.validateToken(token)) { // Doar validarea token-ului
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username, // Principal-ul va fi username-ul
                                null,     // Credentialele sunt null, deoarece token-ul a fost deja verificat
                                authorities // Autoritățile (rolurile) preluate din token
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}