package fr.miage.motus.auth_service.service;

import fr.miage.motus.auth_service.dto.AuthResponse;
import fr.miage.motus.auth_service.dto.LoginRequest;
import fr.miage.motus.auth_service.dto.RegisterRequest;
import fr.miage.motus.auth_service.entity.AuthUser;
import fr.miage.motus.auth_service.repository.AuthUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthUserRepository repository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caractères");
        }
        if (repository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
        }
        AuthUser user = new AuthUser(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(), jwtService.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        AuthUser user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(), jwtService.getExpirationSeconds());
    }
}
