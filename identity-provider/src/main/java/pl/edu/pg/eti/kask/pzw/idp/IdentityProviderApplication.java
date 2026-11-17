package pl.edu.pg.eti.kask.pzw.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OpenID Connect identity provider (authorization server) for the PZW application. Users log in with the login and
 * password stored in the PZW database and receive signed access tokens (JWT) with their roles.
 */
@SpringBootApplication
public class IdentityProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

}
