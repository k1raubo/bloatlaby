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

    public void create(District district) {
        districtRepository.create(district);
    }

    public void update(District district) {
        districtRepository.update(district);
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
