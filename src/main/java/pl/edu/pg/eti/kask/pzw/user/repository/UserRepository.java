package pl.edu.pg.eti.kask.pzw.user.repository;

import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    public synchronized Optional<User> find(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public synchronized List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public synchronized void create(User user) {
        users.put(user.getId(), user);
    }

}
