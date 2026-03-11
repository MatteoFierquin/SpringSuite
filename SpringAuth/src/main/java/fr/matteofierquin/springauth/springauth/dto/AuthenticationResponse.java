package fr.matteofierquin.springauth.springauth.dto;

public record AuthenticationResponse(
    String token,
    String username,
    String role
) {}
