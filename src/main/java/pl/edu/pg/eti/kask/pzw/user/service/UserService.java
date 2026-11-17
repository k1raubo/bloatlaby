package pl.edu.pg.eti.kask.pzw.user.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.user.entity.Role;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional
public class UserService {

    private final UserRepository repository;

    /**
     * Hashes passwords before they are stored; the identity provider verifies them with the same parameters.
     */
    private final Pbkdf2PasswordHash passwordHash;

    /**
     * Required by CDI to create a client proxy.
     */
    protected UserService() {
        this(null, null);
    }

    @Inject
    public UserService(UserRepository repository, Pbkdf2PasswordHash passwordHash) {
        this.repository = repository;
        this.passwordHash = passwordHash;
        if (passwordHash != null) {
            passwordHash.initialize(Map.of(
                    "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256",
                    "Pbkdf2PasswordHash.Iterations", "2048",
                    "Pbkdf2PasswordHash.SaltSizeBytes", "32",
                    "Pbkdf2PasswordHash.KeySizeBytes", "32"));
        }
    }

    public Optional<User> find(UUID id) {
        return repository.find(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Registers a new user. Anybody may register, but only as a regular user; the role set by the caller is ignored.
     *
     * @param password plain password, stored as a hash
     * @throws IllegalArgumentException if a user with the same identifier or login already exists
     */
    public void register(User user, String password) {
        user.setRole(Role.USER);
        create(user, password);
    }

    /**
     * Creates a user with any role. Not exposed to clients, used to load test data.
     *
     * @param password plain password, stored as a hash
     * @throws IllegalArgumentException if a user with the same identifier or login already exists
     */
    public void create(User user, String password) {
        if (repository.find(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User %s already exists".formatted(user.getId()));
        }
        if (repository.findByLogin(user.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Login %s is taken".formatted(user.getLogin()));
        }
        user.setPassword(passwordHash.generate(password.toCharArray()));
        repository.create(user);
    }

}
