package pl.edu.pg.eti.kask.pzw.user.service;

import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> find(UUID id) {
        return repository.find(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void create(User user) {
        repository.create(user);
    }

}
