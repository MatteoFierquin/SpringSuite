package fr.matteofierquin.springauth.springauth.controller;

import fr.matteofierquin.springauth.springauth.dto.AuthenticationRequest;
import fr.matteofierquin.springauth.springauth.dto.AuthenticationResponse;
import fr.matteofierquin.springauth.springauth.dto.RegisterRequest;
import fr.matteofierquin.springauth.springauth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
