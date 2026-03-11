package fr.matteofierquin.springauth.springauth.dto;

import fr.matteofierquin.springauth.springauth.model.Role;

public record AuthenticationRequest(
    String username,
    String password
) {}
