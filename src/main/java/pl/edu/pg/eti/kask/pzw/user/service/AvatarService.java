package pl.edu.pg.eti.kask.pzw.user.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.edu.pg.eti.kask.pzw.user.repository.AvatarRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

@ApplicationScoped
public class AvatarService {

    private final UserService userService;

    private final AvatarRepository avatarRepository;

    /**
     * Required by CDI to create a client proxy.
     */
    protected AvatarService() {
        this(null, null);
    }

    @Inject
    public AvatarService(UserService userService, AvatarRepository avatarRepository) {
        this.userService = userService;
        this.avatarRepository = avatarRepository;
    }

    /**
     * @throws NoSuchElementException if the user or their avatar does not exist
     */
    public synchronized byte[] find(UUID userId) {
        userService.find(userId).orElseThrow();
        return avatarRepository.find(userId).orElseThrow();
    }

    /**
     * @return true if the avatar was created, false if an existing one was overwritten
     * @throws NoSuchElementException if the user does not exist
     */
    public synchronized boolean save(UUID userId, byte[] content) {
        userService.find(userId).orElseThrow();
        boolean created = !avatarRepository.exists(userId);
        avatarRepository.save(userId, content);
        return created;
    }

    /**
     * @throws NoSuchElementException if the user or their avatar does not exist
     */
    public synchronized void delete(UUID userId) {
        find(userId);
        avatarRepository.delete(userId);
    }

}
