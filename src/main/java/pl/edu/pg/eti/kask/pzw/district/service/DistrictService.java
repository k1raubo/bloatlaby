package pl.edu.pg.eti.kask.pzw.district.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.security.AccessDeniedException;
import pl.edu.pg.eti.kask.pzw.security.Authorization;
import pl.edu.pg.eti.kask.pzw.user.entity.UserRoles;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * Access rules: every logged-in user may read districts, only administrators may create, change and delete them.
 */
@ApplicationScoped
@Transactional
public class DistrictService {

    private final DistrictRepository districtRepository;

    private final Authorization authorization;

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictService() {
        this(null, null);
    }

    @Inject
    public DistrictService(DistrictRepository districtRepository, Authorization authorization) {
        this.districtRepository = districtRepository;
        this.authorization = authorization;
    }

    /**
     * @throws AccessDeniedException if the caller is not logged in
     */
    public Optional<District> find(UUID id) {
        authorization.requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        return districtRepository.find(id);
    }

    /**
     * @throws AccessDeniedException if the caller is not logged in
     */
    public List<District> findAll() {
        authorization.requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        return districtRepository.findAll();
    }

    /**
     * @throws IllegalArgumentException if a district with the same identifier or name already exists
     * @throws AccessDeniedException    if the caller is not an administrator
     */
    public void create(District district) {
        authorization.requireAnyRole(UserRoles.ADMIN);
        if (districtRepository.find(district.getId()).isPresent()) {
            throw new IllegalArgumentException("District %s already exists".formatted(district.getId()));
        }
        requireUniqueName(district);
        districtRepository.create(district);
    }

    /**
     * @throws NoSuchElementException   if the district does not exist
     * @throws IllegalArgumentException if another district has the same name
     * @throws AccessDeniedException    if the caller is not an administrator
     */
    public void update(District district) {
        authorization.requireAnyRole(UserRoles.ADMIN);
        District current = districtRepository.find(district.getId()).orElseThrow();
        requireUniqueName(district);
        // Permits are changed only through PermitService; a detached district may carry an outdated list.
        district.setPermits(current.getPermits());
        districtRepository.update(district);
    }

    private void requireUniqueName(District district) {
        districtRepository.findByName(district.getName())
                .filter(other -> !other.getId().equals(district.getId()))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("District named %s already exists".formatted(district.getName()));
                });
    }

    /**
     * Deletes the district; its permits are removed by cascade. The permits are first removed from their owners, so
     * users do not keep deleted permits.
     *
     * @throws NoSuchElementException if the district does not exist
     * @throws AccessDeniedException  if the caller is not an administrator
     */
    public void delete(UUID id) {
        authorization.requireAnyRole(UserRoles.ADMIN);
        District district = districtRepository.find(id).orElseThrow();
        district.getPermits().stream()
                .filter(permit -> permit.getUser() != null)
                .forEach(permit -> permit.getUser().getPermits().remove(permit));
        districtRepository.delete(district);
    }

}
