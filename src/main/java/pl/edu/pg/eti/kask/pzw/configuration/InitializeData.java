package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.repository.DistrictRepository;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.repository.PermitRepository;
import pl.edu.pg.eti.kask.pzw.user.entity.Role;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Fills the database with test data on application startup: users (password = login) and districts with permits owned
 * by the users.
 * <p>
 * Nobody is logged in at startup, so districts and permits are stored directly through the repositories, bypassing the
 * access checks of the business logic (like a database seed script). Users are created by {@link UserService}, which
 * hashes their passwords. Everything is stored in one transaction.
 */
@ApplicationScoped
public class InitializeData {

    private static final Logger LOGGER = Logger.getLogger(InitializeData.class.getName());

    private final UserService userService;

    private final DistrictRepository districtRepository;

    private final PermitRepository permitRepository;

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
    public InitializeData(UserService userService, DistrictRepository districtRepository,
                          PermitRepository permitRepository, RequestContextController requestContextController) {
        this.userService = userService;
        this.districtRepository = districtRepository;
        this.permitRepository = permitRepository;
        this.requestContextController = requestContextController;
    }

    @Transactional
    public void contextInitialized(@Observes @Initialized(ApplicationScoped.class) Object event) {
        requestContextController.activate();
        try {
            init();
        } finally {
            requestContextController.deactivate();
        }
    }

    private void init() {
        user("c4804e0f-769e-4ab9-9ebe-0578fb4f00a6", "admin", LocalDate.of(1980, 5, 14), Role.ADMIN);
        User kowalski = user("81e1c2a9-7f57-439b-b53d-6db88b071e4e", "kowalski", LocalDate.of(1992, 3, 2), Role.USER);
        User nowak = user("ed6cfb2a-cad7-47dd-9b56-9d1e3c7a4197", "nowak", LocalDate.of(2001, 11, 23), Role.USER);
        User wisniewska = user("f5875513-bf7b-4ae1-b8a5-5b70a1b90e76", "wisniewska", LocalDate.of(1975, 8, 30), Role.USER);

        District gdansk = district("0b8e4c52-6a64-4b4f-9a9e-2c1d5b7f3a10", "Okręg PZW Gdańsk", "220.00", 140);
        District elblag = district("4d2f8a7e-1c3b-4e5d-8f6a-9b0c1d2e3f40", "Okręg PZW Elbląg", "185.50", 95);

        permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c01", LocalDate.of(2026, 1, 1), "220.00", true, gdansk, kowalski);
        permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c02", LocalDate.of(2026, 4, 1), "60.00", false, gdansk, nowak);
        permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c03", LocalDate.of(2026, 1, 1), "185.50", false, elblag, kowalski);
        permit("6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c04", LocalDate.of(2026, 5, 1), "90.00", true, elblag, wisniewska);

        LOGGER.info(() -> "Test data loaded: users admin, kowalski, nowak, wisniewska (password = login)");
    }

    private User user(String id, String login, LocalDate birthday, Role role) {
        User user = new User();
        user.setId(UUID.fromString(id));
        user.setLogin(login);
        user.setBirthday(birthday);
        user.setRole(role);
        userService.create(user, login);
        return user;
    }

    private District district(String id, String name, String annualFee, int waterCount) {
        District district = new District();
        district.setId(UUID.fromString(id));
        district.setName(name);
        district.setAnnualFee(new BigDecimal(annualFee));
        district.setWaterCount(waterCount);
        districtRepository.create(district);
        return district;
    }

    /**
     * Stores the permit and adds it to both its district and owner, as the services do.
     */
    private void permit(String id, LocalDate validFrom, String price, boolean boatFishing, District district,
                        User user) {
        Permit permit = new Permit();
        permit.setId(UUID.fromString(id));
        permit.setValidFrom(validFrom);
        permit.setPrice(new BigDecimal(price));
        permit.setBoatFishing(boatFishing);
        permit.setDistrict(district);
        permit.setUser(user);
        permitRepository.create(permit);
        district.getPermits().add(permit);
        user.getPermits().add(permit);
    }

}
