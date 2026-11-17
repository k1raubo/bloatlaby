package pl.edu.pg.eti.kask.pzw.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Bearer token authentication. Called by the container for every request:
 * <ul>
 *     <li>no token - the request continues anonymously, so open resources (e.g. registration) work and secured ones
 *     are rejected by authorization with 401,</li>
 *     <li>valid token - the caller is logged in for this request only (stateless, no session),</li>
 *     <li>invalid token (bad signature, expired, foreign issuer) - 401 at once.</li>
 * </ul>
 */
@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String BEARER = "Bearer ";

    private final IdentityStoreHandler identityStoreHandler;

    /**
     * Required by CDI to create a client proxy.
     */
    protected JwtAuthenticationMechanism() {
        this(null);
    }

    @Inject
    public JwtAuthenticationMechanism(IdentityStoreHandler identityStoreHandler) {
        this.identityStoreHandler = identityStoreHandler;
    }

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
                                                HttpMessageContext context) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            return context.isProtected() ? challenge(response, context, null) : context.doNothing();
        }
        CredentialValidationResult result = identityStoreHandler.validate(
                new JwtCredential(header.substring(BEARER.length()).strip()));
        if (result.getStatus() != CredentialValidationResult.Status.VALID) {
            return challenge(response, context, "invalid_token");
        }
        return context.notifyContainerAboutLogin(result);
    }

    /**
     * Responds with 401 and tells the client to authenticate with a bearer token.
     */
    static AuthenticationStatus challenge(HttpServletResponse response, HttpMessageContext context, String error) {
        response.setHeader("WWW-Authenticate", error == null ? "Bearer" : "Bearer error=\"%s\"".formatted(error));
        return context.responseUnauthorized();
    }

}
