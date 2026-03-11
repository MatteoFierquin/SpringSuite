package fr.matteofierquin.springauth.springauth.service;

import fr.matteofierquin.springauth.springauth.dto.AuthenticationRequest;
import fr.matteofierquin.springauth.springauth.dto.AuthenticationResponse;
import fr.matteofierquin.springauth.springauth.dto.RegisterRequest;
import fr.matteofierquin.springauth.springauth.model.Role;
import fr.matteofierquin.springauth.springauth.model.User;
import fr.matteofierquin.springauth.springauth.repository.UserRepository;
import fr.matteofierquin.springauth.springauth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.USER)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }
}
