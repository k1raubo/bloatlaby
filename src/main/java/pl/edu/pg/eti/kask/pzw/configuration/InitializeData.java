package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.user.entity.Role;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Fills the database with test data on application startup and demonstrates district and permit services.
 */
@ApplicationScoped
public class InitializeData {

    private static final Logger LOGGER = Logger.getLogger(InitializeData.class.getName());

    private final UserService userService;

    private final DistrictService districtService;

    private final PermitService permitService;

    /**
     * Repositories are request scoped, so the request context has to be activated outside HTTP requests.
     */
    private final RequestContextController requestContextController;

    /**
     * Required by CDI to create a client proxy.
     */
    protected InitializeData() {
        this(null, null, null, null);
    }

    @Inject
    public InitializeData(UserService userService, DistrictService districtService, PermitService permitService,
                          RequestContextController requestContextController) {
        this.userService = userService;
        this.districtService = districtService;
        this.permitService = permitService;
        this.requestContextController = requestContextController;
    }

    public void contextInitialized(@Observes @Initialized(ApplicationScoped.class) Object event) {
        requestContextController.activate();
        try {
            init();
            demonstrateServices();
        } finally {
            requestContextController.deactivate();
        }
    }

    private void init() {
        userService.create(user("c4804e0f-769e-4ab9-9ebe-0578fb4f00a6", "admin", LocalDate.of(1980, 5, 14), Role.ADMIN));
        userService.create(user("81e1c2a9-7f57-439b-b53d-6db88b071e4e", "kowalski", LocalDate.of(1992, 3, 2), Role.USER));
        userService.create(user("ed6cfb2a-cad7-47dd-9b56-9d1e3c7a4197", "nowak", LocalDate.of(2001, 11, 23), Role.USER));
        userService.create(user("f5875513-bf7b-4ae1-b8a5-5b70a1b90e76", "wisniewska", LocalDate.of(1975, 8, 30), Role.USER));

        District gdansk = district("0b8e4c52-6a64-4b4f-9a9e-2c1d5b7f3a10", "Okręg PZW Gdańsk", "220.00", 140);
        District elblag = district("4d2f8a7e-1c3b-4e5d-8f6a-9b0c1d2e3f40", "Okręg PZW Elbląg", "185.50", 95);
        districtService.create(gdansk);
        districtService.create(elblag);

        permitService.create(permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c01", LocalDate.of(2026, 1, 1), "220.00", true, gdansk));
        permitService.create(permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c02", LocalDate.of(2026, 4, 1), "60.00", false, gdansk));
        permitService.create(permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c03", LocalDate.of(2026, 1, 1), "185.50", false, elblag));
    }

    /**
     * Shows that district and permit services work (no controllers are required at this stage).
     */
    private void demonstrateServices() {
        districtService.findAll().forEach(district -> LOGGER.info(() -> "District %s: %s, fee %s, waters %d, permits %d"
                .formatted(district.getId(), district.getName(), district.getAnnualFee(), district.getWaterCount(),
                        permitService.findAllByDistrict(district.getId()).size())));
        permitService.findAll().forEach(permit -> LOGGER.info(() -> "Permit %s: district %s, from %s, price %s, boat %b"
                .formatted(permit.getId(), permit.getDistrict().getName(), permit.getValidFrom(), permit.getPrice(),
                        permit.isBoatFishing())));

        District demo = district(UUID.randomUUID().toString(), "Okręg demonstracyjny", "100.00", 10);
        districtService.create(demo);
        Permit permit = permit(UUID.randomUUID().toString(), LocalDate.of(2026, 6, 1), "100.00", false, demo);
        permitService.create(permit);
        LOGGER.info(() -> "Created demo district with %d permit(s)"
                .formatted(permitService.findAllByDistrict(demo.getId()).size()));

        permit.setPrice(new BigDecimal("80.00"));
        permitService.update(permit);
        LOGGER.info(() -> "Updated demo permit price: %s"
                .formatted(permitService.find(permit.getId()).orElseThrow().getPrice()));

        districtService.delete(demo.getId());
        LOGGER.info(() -> "Deleted demo district, exists: %b, demo permit exists: %b, districts: %d, permits: %d"
                .formatted(districtService.find(demo.getId()).isPresent(), permitService.find(permit.getId()).isPresent(),
                        districtService.findAll().size(), permitService.findAll().size()));
    }

    private static User user(String id, String login, LocalDate birthday, Role role) {
        User user = new User();
        user.setId(UUID.fromString(id));
        user.setLogin(login);
        user.setBirthday(birthday);
        user.setRole(role);
        return user;
    }

    private static District district(String id, String name, String annualFee, int waterCount) {
        District district = new District();
        district.setId(UUID.fromString(id));
        district.setName(name);
        district.setAnnualFee(new BigDecimal(annualFee));
        district.setWaterCount(waterCount);
        return district;
    }

    private static Permit permit(String id, LocalDate validFrom, String price, boolean boatFishing, District district) {
        Permit permit = new Permit();
        permit.setId(UUID.fromString(id));
        permit.setValidFrom(validFrom);
        permit.setPrice(new BigDecimal(price));
        permit.setBoatFishing(boatFishing);
        permit.setDistrict(district);
        return permit;
    }

}
