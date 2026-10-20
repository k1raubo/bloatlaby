package pl.edu.pg.eti.kask.pzw.user.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.edu.pg.eti.kask.pzw.repository.Repository;
import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class UserRepository implements Repository<User, UUID> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> find(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("select u from User u order by u.login", User.class).getResultList();
    }

    @Override
    public void create(User entity) {
        em.persist(entity);
    }

    @Override
    public void update(User entity) {
        em.merge(entity);
    }

    @Override
    public void delete(User entity) {
        em.remove(em.find(User.class, entity.getId()));
    }

}
