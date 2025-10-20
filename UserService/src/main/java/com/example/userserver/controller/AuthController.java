package com.example.userserver.controller;

import com.example.userserver.config.JwtUtil;
import com.example.userserver.domain.User;
import com.example.userserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
//        // 1) Authenticate credentials
//        var authToken = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
//        authenticationManager.authenticate(authToken);
//
//        // 2) Load UserDetails and then the JPA User to get userType
//        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
//        User user = userRepository.findByUsername(req.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // 3) Generate a JWT with the single 'role' claim
//        String token = jwtUtil.generateToken(userDetails, user.getUserType().name());
//
//        // 4) Return it
//        return ResponseEntity.ok(new AuthResponse(token));
//    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        // 1) Authenticate credentials
        var authToken = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        authenticationManager.authenticate(authToken);

        // 2) Load UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());

        // 3) Get the first role from authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        // 4) Generate a JWT with the role claim
        String token = jwtUtil.generateToken(userDetails, role);

        // 5) Return token in response
        return ResponseEntity.ok(new AuthResponse(token));
    }
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Auth microservice is up");
    }
}
