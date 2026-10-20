package pl.edu.pg.eti.kask.pzw.district.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class DistrictRepository implements Repository<District, UUID> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<District> find(UUID id) {
        return Optional.ofNullable(em.find(District.class, id));
    }

    @Override
    public List<District> findAll() {
        return em.createQuery("select d from District d order by d.name", District.class).getResultList();
    }

    @Override
    public void create(District entity) {
        em.persist(entity);
    }

    @Override
    public void update(District entity) {
        em.merge(entity);
    }

    @Override
    public void delete(District entity) {
        em.remove(em.find(District.class, entity.getId()));
    }

}
