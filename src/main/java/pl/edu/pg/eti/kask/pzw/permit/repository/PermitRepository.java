package pl.edu.pg.eti.kask.pzw.permit.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class PermitRepository implements Repository<Permit, UUID> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Permit> find(UUID id) {
        return Optional.ofNullable(em.find(Permit.class, id));
    }

    @Override
    public List<Permit> findAll() {
        return em.createQuery("select p from Permit p order by p.validFrom", Permit.class).getResultList();
    }

    /**
     * @param login login of the owner
     */
    public List<Permit> findAllByOwner(String login) {
        return em.createQuery("select p from Permit p where p.user.login = :login order by p.validFrom, p.id",
                        Permit.class)
                .setParameter("login", login)
                .getResultList();
    }

    /**
     * @param login login of the owner
     */
    public List<Permit> findAllByDistrictAndOwner(District district, String login) {
        return em.createQuery("select p from Permit p where p.district = :district and p.user.login = :login "
                        + "order by p.validFrom, p.id", Permit.class)
                .setParameter("district", district)
                .setParameter("login", login)
                .getResultList();
    }

    @Override
    public void create(Permit entity) {
        em.persist(entity);
    }

    @Override
    public void update(Permit entity) {
        em.merge(entity);
    }

    @Override
    public void delete(Permit entity) {
        em.remove(em.find(Permit.class, entity.getId()));
    }

}
