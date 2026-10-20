package pl.edu.pg.eti.kask.pzw.user.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AvatarRepository {

    private final Path directory;

    /**
     * Required by CDI to create a client proxy.
     */
    protected AvatarRepository() {
        this.directory = null;
    }

    /**
     * @param context servlet context holding the {@code avatarDirectory} init parameter from the deployment descriptor
     */
    @Inject
    public AvatarRepository(ServletContext context) {
        try {
            this.directory = Files.createDirectories(Path.of(context.getInitParameter("avatarDirectory")).toAbsolutePath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean exists(UUID userId) {
        return Files.exists(file(userId));
    }

    public Optional<byte[]> find(UUID userId) {
        try {
            return exists(userId) ? Optional.of(Files.readAllBytes(file(userId))) : Optional.empty();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void save(UUID userId, byte[] content) {
        try {
            Files.write(file(userId), content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void delete(UUID userId) {
        try {
            Files.delete(file(userId));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path file(UUID userId) {
        return directory.resolve(userId.toString());
    }

}
