package pl.edu.pg.eti.kask.pzw.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates access tokens issued by the identity provider: the signature is checked with the provider's public keys
 * (JWKS, fetched and cached, refreshed when the provider starts signing with a new key), then the issuer, audience and
 * expiration. The caller is the token subject (login), the groups are taken from the {@code roles} claim.
 */
@ApplicationScoped
public class JwtIdentityStore implements IdentityStore {

    private static final Logger LOGGER = Logger.getLogger(JwtIdentityStore.class.getName());

    private static final String ROLES_CLAIM = "roles";

    private final ServletContext servletContext;

    private ConfigurableJWTProcessor<SecurityContext> processor;

    /**
     * Required by CDI to create a client proxy.
     */
    protected JwtIdentityStore() {
        this(null);
    }

    @Inject
    public JwtIdentityStore(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Issuer, audience and key set address come from the {@code jwt.*} context parameters in {@code web.xml}.
     */
    @PostConstruct
    void init() {
        String issuer = servletContext.getInitParameter("jwt.issuer");
        String audience = servletContext.getInitParameter("jwt.audience");
        JWKSource<SecurityContext> keys;
        try {
            keys = JWKSourceBuilder.create(URI.create(servletContext.getInitParameter("jwt.jwksUri")).toURL()).build();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid jwt.jwksUri", e);
        }
        processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keys));
        processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(audience,
                new JWTClaimsSet.Builder().issuer(issuer).build(), Set.of("sub", "exp")));
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof JwtCredential jwt)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        try {
            JWTClaimsSet claims = processor.process(jwt.token(), null);
            List<String> roles = claims.getStringListClaim(ROLES_CLAIM);
            return new CredentialValidationResult(claims.getSubject(),
                    roles == null ? Set.of() : new HashSet<>(roles));
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Rejected token", e);
            return CredentialValidationResult.INVALID_RESULT;
        }
    }

}
