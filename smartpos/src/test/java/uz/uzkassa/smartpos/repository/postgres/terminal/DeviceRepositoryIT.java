package uz.uzkassa.smartpos.repository.postgres.terminal;

import net.ttddyy.dsproxy.QueryCountHolder;
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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class DeviceRepositoryIT {
    @Autowired
    DeviceRepository deviceRepository;

    @BeforeEach
    public void setup() {
        List<Device> devices = EnumSet.allOf(DeviceType.class).stream().map(deviceType -> {
            Device device = new Device();
            device.setType(deviceType);
            device.setCompanyId(2L);
            device.setBranchId(2L);
            device.setSerialNumber(RandomStringUtils.randomAlphanumeric(10, 15));
            return device;
        }).toList();
        deviceRepository.saveAll(devices);
    }

    @AfterEach
    public void cleanup() {
        deviceRepository.deleteAll();
    }

    @Test
    void findAllByCompanyId() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByCompanyId(2L);
        EnumSet<DeviceType> deviceTypes = EnumSet.allOf(DeviceType.class);
        assertEquals(deviceTypes.size(), devices.size());
        deviceTypes.forEach(deviceType -> assertEquals(1, devices.stream().filter(device -> device.getType().equals(deviceType)).count()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByBranchId() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByBranchId(2L);
        EnumSet<DeviceType> deviceTypes = EnumSet.allOf(DeviceType.class);
        assertEquals(deviceTypes.size(), devices.size());
        deviceTypes.forEach(deviceType -> assertEquals(1, devices.stream().filter(device -> device.getType().equals(deviceType)).count()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllBySerialNumber() {
        QueryCountHolder.clear();
        Device device = deviceRepository.findAll().stream().findFirst().orElseThrow();
        var deviceOptional = deviceRepository.findAllBySerialNumber(device.getSerialNumber());
        assertFalse(deviceOptional.isEmpty());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByBranchIdAndTypeIn() {
        QueryCountHolder.clear();
        EnumSet<DeviceType> deviceTypes = EnumSet.allOf(DeviceType.class);
        deviceTypes.forEach(deviceType -> assertEquals(1, deviceRepository.findAllByBranchIdAndTypeIn(2L, List.of(deviceType)).size()));
        assertEquals(deviceTypes.size(), QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void companyTerminalsCount() {
        QueryCountHolder.clear();
        int devices = deviceRepository.companyTerminalsCount(2L);
        assertEquals(1, devices);
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByCompanyIdInAndType() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByCompanyIdInAndType(List.of(2L), DeviceType.FISCAL);
        assertEquals(1, devices.size());
        devices.forEach(device -> assertEquals(DeviceType.FISCAL, device.getType()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findFirstBySerialNumberAndType() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAll();
        var device = devices.getFirst();
        Optional<Device> optionalDevice = deviceRepository.findFirstBySerialNumberAndType(device.getSerialNumber(), device.getType());
        assertTrue(optionalDevice.isPresent());
        assertEquals(device.getId(),optionalDevice.get().getId());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findFirstByBranchIdAndType() {
        QueryCountHolder.clear();
        Optional<Device> terminal = deviceRepository.findFirstByBranchIdAndType(2L, DeviceType.FISCAL);
        assertTrue(terminal.isPresent());
        assertEquals(DeviceType.FISCAL, terminal.get().getType());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByCompanyIdAndType() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByCompanyIdAndType(2L, DeviceType.FISCAL);
        assertEquals(1, devices.size());
        devices.forEach(device -> assertEquals(DeviceType.FISCAL, device.getType()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByBranchIdAndType() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByBranchIdAndType(2L, DeviceType.FISCAL);
        assertEquals(1, devices.size());
        devices.forEach(device -> assertEquals(DeviceType.FISCAL, device.getType()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByBranchIdInAndType() {
        QueryCountHolder.clear();
        List<Device> devices = deviceRepository.findAllByBranchIdInAndType(List.of(2L), DeviceType.FISCAL);
        assertEquals(1, devices.size());
        devices.forEach(device -> assertEquals(DeviceType.FISCAL, device.getType()));
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
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