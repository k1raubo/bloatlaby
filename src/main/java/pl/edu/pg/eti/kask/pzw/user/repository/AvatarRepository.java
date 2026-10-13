package pl.edu.pg.eti.kask.pzw.user.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class AvatarRepository {

    private final Path directory;

    public AvatarRepository(Path directory) {
        try {
            this.directory = Files.createDirectories(directory.toAbsolutePath());
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
