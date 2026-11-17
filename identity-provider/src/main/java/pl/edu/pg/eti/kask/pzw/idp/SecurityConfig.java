package pl.edu.pg.eti.kask.pzw.idp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Authorization server with OpenID Connect. Endpoints (published under {@code /.well-known/openid-configuration}):
 * {@code /oauth2/authorize}, {@code /oauth2/token}, {@code /oauth2/jwks}, {@code /userinfo}. Signing keys (RSA) are
 * generated at startup by Spring Boot.
 */
@Configuration
public class SecurityConfig {

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Claim with the user's roles, read by the PZW application.
     */
    private static final String ROLES_CLAIM = "roles";

    /**
     * Protocol endpoints. A browser that is not logged in is sent to the login form.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServer.getEndpointsMatcher())
                .with(authorizationServer, server -> server.oidc(Customizer.withDefaults()))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    /**
     * Everything else, including the login form.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Users of the PZW application, read from its database (login, password hash, role).
     */
    @Bean
    public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate) {
        return login -> {
            List<UserDetails> users = jdbcTemplate.query("select login, password, role from users where login = ?",
                    (row, number) -> User.withUsername(row.getString("login"))
                            .password(row.getString("password"))
                            .roles(row.getString("role"))
                            .build(),
                    login);
            if (users.isEmpty()) {
                throw new UsernameNotFoundException(login);
            }
            return users.getFirst();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new JakartaPbkdf2PasswordEncoder();
    }

    /**
     * The PZW application as the only client. It uses the authorization code flow; tokens are issued for it (the aud
     * claim is its identifier) and are valid for 30 minutes.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder,
                                                                 @Value("${pzw.client.id}") String clientId,
                                                                 @Value("${pzw.client.secret}") String clientSecret,
                                                                 @Value("${pzw.client.redirect-uri}") String redirectUri) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(redirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(30)).build())
                .build();
        return new InMemoryRegisteredClientRepository(client);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(@Value("${pzw.issuer}") String issuer) {
        return AuthorizationServerSettings.builder().issuer(issuer).build();
    }

    /**
     * Adds the user's roles (without the {@code ROLE_} prefix, e.g. {@code ["ADMIN"]}) to access and ID tokens.
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> rolesTokenCustomizer() {
        return context -> {
            String tokenType = context.getTokenType().getValue();
            if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(tokenType) || OidcParameterNames.ID_TOKEN.equals(tokenType)) {
                List<String> roles = context.getPrincipal().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith(ROLE_PREFIX))
                        .map(authority -> authority.substring(ROLE_PREFIX.length()))
                        .toList();
                context.getClaims().claim(ROLES_CLAIM, roles);
            }
        };
    }

}
