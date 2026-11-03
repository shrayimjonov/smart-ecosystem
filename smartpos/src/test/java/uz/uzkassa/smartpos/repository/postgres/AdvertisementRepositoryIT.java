package uz.uzkassa.smartpos.repository.postgres;

import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.Advertisement;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementAdminDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementDeviceListDto;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.filter.AdvertisementFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class AdvertisementRepositoryIT {
    @Autowired
    AdvertisementRepository advertisementRepository;
    private final Device device = Instancio.create(Device.class);

    @BeforeEach
    public void setup() {
        var advertisements = new ArrayList<Advertisement>();
        var advertisement = new Advertisement();
        advertisement.setType(NotificationType.PUBLIC);
        advertisement.setVideoUrl(Instancio.gen().string().minLength(50).get());
        advertisements.add(advertisement);

        advertisement = new Advertisement();
        advertisement.setType(NotificationType.PUBLIC);
        advertisement.setDeviceTypes(List.of(device.getType(), DeviceType.FISCAL));
        advertisement.setVideoUrl(Instancio.gen().string().minLength(50).get());
        advertisements.add(advertisement);

        advertisement = new Advertisement();
        advertisement.setType(NotificationType.PRIVATE);
        advertisement.setCompanyId(device.getCompanyId());
        advertisement.setVideoUrl(Instancio.gen().string().minLength(50).get());
        advertisements.add(advertisement);

        advertisement = new Advertisement();
        advertisement.setType(NotificationType.PRIVATE);
        advertisement.setCompanyId(device.getCompanyId());
        advertisement.setDeviceTypes(List.of(device.getType(), DeviceType.FISCAL));
        advertisement.setVideoUrl(Instancio.gen().string().minLength(50).get());
        advertisements.add(advertisement);

        advertisement = new Advertisement();
        advertisement.setType(NotificationType.PRIVATE);
        advertisement.setCompanyId(device.getCompanyId());
        advertisement.setSerialNumbers(List.of(device.getSerialNumber(), Instancio.gen().string().minLength(10).get()));
        advertisement.setVideoUrl(Instancio.gen().string().minLength(50).get());
        advertisements.add(advertisement);

        advertisementRepository.saveAll(advertisements);
    }

    @AfterEach
    public void cleanup() {
        advertisementRepository.deleteAll();
    }

    @Test
    void advertisementForDevice() {
        List<AdvertisementDeviceListDto> advertisementList = advertisementRepository.advertisementForDevice(device);
        assertEquals(5, advertisementList.size());
    }

    @Test
    void findAllByFilter_With_AllFilters() {
        AdvertisementFilter filter = Instancio.create(AdvertisementFilter.class);
        PageDto<AdvertisementAdminDto> advertisements = advertisementRepository.findAllByFilter(filter);
        assertEquals(0, advertisements.getContent().size());
        assertEquals(0, advertisements.getTotalPages());
        assertEquals(0,  advertisements.getTotalElements());
    }

    @Test
    void findAllByFilter_When_FilterIsEmpty() {
        AdvertisementFilter filter = new AdvertisementFilter();
        PageDto<AdvertisementAdminDto> advertisements = advertisementRepository.findAllByFilter(filter);
        assertEquals(5, advertisements.getContent().size());
        assertEquals(1, advertisements.getTotalPages());
        assertEquals(5, advertisements.getTotalElements());
    }

    @BeforeAll
    static void setUp() {
        postgreSQL.start();
    }

    @AfterAll
    static void tearDown() {
        postgreSQL.stop();
    }

    public static final CustomPostgreSQLContainer postgreSQL = CustomPostgreSQLContainer.getInstance();
}