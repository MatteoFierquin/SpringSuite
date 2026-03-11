package fr.matteofierquin.springauth.springauth.dto;

import fr.matteofierquin.springauth.springauth.model.Role;

public record RegisterRequest(
    String username,
    String email,
    String password,
    Role role
) {}
