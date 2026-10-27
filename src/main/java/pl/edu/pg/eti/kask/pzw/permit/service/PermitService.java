package pl.edu.pg.eti.kask.pzw.permit.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.repository.PermitRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional
public class PermitService {

    private final PermitRepository permitRepository;

    private final DistrictRepository districtRepository;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitService() {
        this(null, null);
    }

    @Inject
    public PermitService(PermitRepository permitRepository, DistrictRepository districtRepository) {
        this.permitRepository = permitRepository;
        this.districtRepository = districtRepository;
    }

    public Optional<Permit> find(UUID id) {
        return permitRepository.find(id);
    }

    public List<Permit> findAll() {
        return permitRepository.findAll();
    }

    /**
     * @throws NoSuchElementException if the district does not exist
     */
    public List<Permit> findAllByDistrict(UUID districtId) {
        return permitRepository.findAllByDistrict(districtRepository.find(districtId).orElseThrow());
    }

    /**
     * @throws NoSuchElementException if the permit's district does not exist
     */
    public void create(Permit permit) {
        attachDistrict(permit);
        permitRepository.create(permit);
    }

    /**
     * @throws NoSuchElementException if the permit or its district does not exist
     */
    public void update(Permit permit) {
        permitRepository.find(permit.getId()).orElseThrow();
        attachDistrict(permit);
        permitRepository.update(permit);
    }

    /**
     * Replaces the district reference (possibly holding only an identifier) with the persisted district.
     */
    private void attachDistrict(Permit permit) {
        permit.setDistrict(districtRepository.find(permit.getDistrict().getId()).orElseThrow());
    }

    /**
     * @throws NoSuchElementException if the permit does not exist
     */
    public void delete(UUID id) {
        permitRepository.delete(permitRepository.find(id).orElseThrow());
    }

}
