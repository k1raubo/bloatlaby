package pl.edu.pg.eti.kask.pzw.permit.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.repository.PermitRepository;
import pl.edu.pg.eti.kask.pzw.security.AccessDeniedException;
import pl.edu.pg.eti.kask.pzw.security.Authorization;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.entity.UserRoles;
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
 * <p>
 * Access rules: administrators see and change all permits, users only their own ones (filtered by the database
 * queries), and the owner of a new permit is always the caller.
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

    private final Authorization authorization;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitService() {
        this(null, null, null, null);
    }

    @Inject
    public PermitService(PermitRepository permitRepository, DistrictRepository districtRepository,
                         UserRepository userRepository, Authorization authorization) {
        this.permitRepository = permitRepository;
        this.districtRepository = districtRepository;
        this.userRepository = userRepository;
        this.authorization = authorization;
    }

    /**
     * @throws AccessDeniedException if the permit exists but the caller is neither its owner nor an administrator
     */
    public Optional<Permit> find(UUID id) {
        Optional<Permit> permit = permitRepository.find(id);
        permit.ifPresent(found -> authorization.requireAdminOrOwner(found.getUser()));
        return permit;
    }

    /**
     * @return the permit only if it was issued for the given district
     * @throws AccessDeniedException if the permit exists but the caller is neither its owner nor an administrator
     */
    public Optional<Permit> find(UUID districtId, UUID id) {
        Optional<Permit> permit = permitRepository.find(id)
                .filter(found -> found.getDistrict().getId().equals(districtId));
        permit.ifPresent(found -> authorization.requireAdminOrOwner(found.getUser()));
        return permit;
    }

    /**
     * @return all permits for an administrator, the caller's own permits for a user
     */
    public List<Permit> findAll() {
        authorization.requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        if (authorization.isAdmin()) {
            return permitRepository.findAll();
        }
        return permitRepository.findAllByOwner(authorization.callerLogin());
    }

    /**
     * @return permits of the district, ordered by {@link #BY_VALID_FROM}: all of them (taken from the district itself)
     * for an administrator, only the caller's own ones (filtered by the query) for a user
     * @throws NoSuchElementException if the district does not exist
     */
    public List<Permit> findAllByDistrict(UUID districtId) {
        authorization.requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        District district = districtRepository.find(districtId).orElseThrow();
        if (authorization.isAdmin()) {
            return district.getPermits().stream().sorted(BY_VALID_FROM).toList();
        }
        return permitRepository.findAllByDistrictAndOwner(district, authorization.callerLogin());
    }

    /**
     * Adds the permit to its district and to its owner, which is always the caller (any owner set by the client is
     * ignored).
     *
     * @throws NoSuchElementException   if the permit's district does not exist
     * @throws IllegalArgumentException if a permit with the same identifier already exists
     */
    public void create(Permit permit) {
        authorization.requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        if (permitRepository.find(permit.getId()).isPresent()) {
            throw new IllegalArgumentException("Permit %s already exists".formatted(permit.getId()));
        }
        District district = districtRepository.find(permit.getDistrict().getId()).orElseThrow();
        User owner = userRepository.findByLogin(authorization.callerLogin()).orElseThrow(
                () -> new AccessDeniedException("Caller %s has no account".formatted(authorization.callerLogin())));
        permit.setDistrict(district);
        permit.setUser(owner);
        permitRepository.create(permit);
        district.getPermits().add(permit);
        owner.getPermits().add(permit);
    }

    /**
     * Updates the permit, moving it to the permits of its new district if the district changed. The owner cannot be
     * changed this way and is kept as it is.
     *
     * @throws NoSuchElementException if the permit or its district does not exist
     * @throws AccessDeniedException  if the caller is neither the owner nor an administrator
     */
    public void update(Permit permit) {
        Permit current = permitRepository.find(permit.getId()).orElseThrow();
        authorization.requireAdminOrOwner(current.getUser());
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
     * @throws AccessDeniedException  if the caller is neither the owner nor an administrator
     */
    public void delete(UUID id) {
        Permit permit = permitRepository.find(id).orElseThrow();
        authorization.requireAdminOrOwner(permit.getUser());
        permit.getDistrict().getPermits().remove(permit);
        if (permit.getUser() != null) {
            permit.getUser().getPermits().remove(permit);
        }
        permitRepository.delete(permit);
    }

}
