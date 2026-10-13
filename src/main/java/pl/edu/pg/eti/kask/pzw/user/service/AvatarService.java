package pl.edu.pg.eti.kask.pzw.user.service;

import pl.edu.pg.eti.kask.pzw.user.repository.AvatarRepository;
import pl.edu.pg.eti.kask.pzw.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

public class AvatarService {

    private final UserRepository userRepository;

    private final AvatarRepository avatarRepository;

    public AvatarService(UserRepository userRepository, AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
    }

    /**
     * @throws NoSuchElementException if the user or their avatar does not exist
     */
    public synchronized byte[] find(UUID userId) {
        userRepository.find(userId).orElseThrow();
        return avatarRepository.find(userId).orElseThrow();
    }

    /**
     * @return true if the avatar was created, false if an existing one was overwritten
     * @throws NoSuchElementException if the user does not exist
     */
    public synchronized boolean save(UUID userId, byte[] content) {
        userRepository.find(userId).orElseThrow();
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
