package pl.edu.pg.eti.kask.pzw.district.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.permit.repository.PermitRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional
public class DistrictService {

    private final DistrictRepository districtRepository;

    private final PermitRepository permitRepository;

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictService() {
        this(null, null);
    }

    @Inject
    public DistrictService(DistrictRepository districtRepository, PermitRepository permitRepository) {
        this.districtRepository = districtRepository;
        this.permitRepository = permitRepository;
    }

    public Optional<District> find(UUID id) {
        return districtRepository.find(id);
    }

    public List<District> findAll() {
        return districtRepository.findAll();
    }

    /**
     * @throws IllegalArgumentException if a district with the same identifier or name already exists
     */
    public void create(District district) {
        if (districtRepository.find(district.getId()).isPresent()) {
            throw new IllegalArgumentException("District %s already exists".formatted(district.getId()));
        }
        requireUniqueName(district);
        districtRepository.create(district);
    }

    /**
     * @throws NoSuchElementException   if the district does not exist
     * @throws IllegalArgumentException if another district has the same name
     */
    public void update(District district) {
        districtRepository.find(district.getId()).orElseThrow();
        requireUniqueName(district);
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
     * Deletes the district together with all permits issued for it.
     *
     * @throws NoSuchElementException if the district does not exist
     */
    public void delete(UUID id) {
        District district = districtRepository.find(id).orElseThrow();
        permitRepository.findAllByDistrict(district).forEach(permitRepository::delete);
        districtRepository.delete(district);
    }

}
