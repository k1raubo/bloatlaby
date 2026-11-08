package pl.edu.pg.eti.kask.pzw.permit.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.repository.PermitRepository;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * Permits are the owning side of their relationships with districts and users. Every change of a permit's district or
 * owner is applied to both sides, so districts and users kept in the persistence context (and the shared cache) never
 * list stale permits.
 */
@ApplicationScoped
@Transactional
public class PermitService {

    /**
     * Order of permits presented to clients. {@code @OrderBy} of the district's permits applies only when they are
     * loaded from the database, permits added later are appended at the end of the list.
     */
    public static final Comparator<Permit> BY_VALID_FROM = Comparator.comparing(Permit::getValidFrom)
            .thenComparing(Permit::getId);

    private final PermitRepository permitRepository;

    private final DistrictRepository districtRepository;

    private final UserRepository userRepository;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitService() {
        this(null, null, null);
    }

    @Inject
    public PermitService(PermitRepository permitRepository, DistrictRepository districtRepository,
                         UserRepository userRepository) {
        this.permitRepository = permitRepository;
        this.districtRepository = districtRepository;
        this.userRepository = userRepository;
    }

    public Optional<Permit> find(UUID id) {
        return permitRepository.find(id);
    }

    public List<Permit> findAll() {
        return permitRepository.findAll();
    }

    /**
     * @return the permit only if it was issued for the given district
     */
    public Optional<Permit> find(UUID districtId, UUID id) {
        return permitRepository.find(id).filter(permit -> permit.getDistrict().getId().equals(districtId));
    }

    /**
     * @return permits of the district, taken from the district itself (no separate query), ordered by
     * {@link #BY_VALID_FROM}
     * @throws NoSuchElementException if the district does not exist
     */
    public List<Permit> findAllByDistrict(UUID districtId) {
        return districtRepository.find(districtId).orElseThrow().getPermits().stream().sorted(BY_VALID_FROM).toList();
    }

    /**
     * Adds the permit to its district and, if it has one, to its owner.
     *
     * @throws NoSuchElementException   if the permit's district or owner does not exist
     * @throws IllegalArgumentException if a permit with the same identifier already exists
     */
    public void create(Permit permit) {
        if (permitRepository.find(permit.getId()).isPresent()) {
            throw new IllegalArgumentException("Permit %s already exists".formatted(permit.getId()));
        }
        District district = districtRepository.find(permit.getDistrict().getId()).orElseThrow();
        User user = permit.getUser() == null ? null : userRepository.find(permit.getUser().getId()).orElseThrow();
        permit.setDistrict(district);
        permit.setUser(user);
        permitRepository.create(permit);
        district.getPermits().add(permit);
        if (user != null) {
            user.getPermits().add(permit);
        }
    }

    /**
     * Updates the permit, moving it to the permits of its new district if the district changed. The owner cannot be
     * changed this way and is kept as it is.
     *
     * @throws NoSuchElementException if the permit or its district does not exist
     */
    public void update(Permit permit) {
        Permit current = permitRepository.find(permit.getId()).orElseThrow();
        District district = districtRepository.find(permit.getDistrict().getId()).orElseThrow();
        if (current.getDistrict() != district) {
            current.getDistrict().getPermits().remove(current);
            district.getPermits().add(current);
        }
        permit.setDistrict(district);
        permit.setUser(current.getUser());
        permitRepository.update(permit);
    }

    /**
     * Removes the permit from its district and owner before deleting it.
     *
     * @throws NoSuchElementException if the permit does not exist
     */
    public void delete(UUID id) {
        Permit permit = permitRepository.find(id).orElseThrow();
        permit.getDistrict().getPermits().remove(permit);
        if (permit.getUser() != null) {
            permit.getUser().getPermits().remove(permit);
        }
        permitRepository.delete(permit);
    }

}
