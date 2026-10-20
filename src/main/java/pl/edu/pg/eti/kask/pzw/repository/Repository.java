package pl.edu.pg.eti.kask.pzw.repository;

import java.util.List;
import java.util.Optional;

/**
 * Basic data access operations for entities of type {@code E} identified by keys of type {@code K}.
 */
public interface Repository<E, K> {

    Optional<E> find(K id);

    List<E> findAll();

    void create(E entity);

    void update(E entity);

    void delete(E entity);

}
