package pl.edu.pg.eti.kask.pzw.security;

import jakarta.security.enterprise.credential.Credential;

/**
 * Access token (JWT) sent by the client in the {@code Authorization: Bearer} header.
 */
public record JwtCredential(String token) implements Credential {
}
