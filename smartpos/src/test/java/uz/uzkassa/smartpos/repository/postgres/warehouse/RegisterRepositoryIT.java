package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Register;
import uz.uzkassa.smartpos.dto.ReportDTO;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.filter.RegisterFilter;
import uz.uzkassa.smartpos.dto.warehouse.RegisterDto;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class RegisterRepositoryIT {
    @Autowired
    private RegisterRepository registerRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private CategoryBranchRepository categoryBranchRepository;
    @Autowired
    private ProductBranchRepository productBranchRepository;
    @Autowired
    private EntityManager entityManager;

    private final Long BRANCH_ID = 2L;
    private final Long PRODUCT_ID = 1L;
    private final Long CITY_ID = 9L;
    private final Long COMPANY_ID = 2L;
    private final Long UNIT_ID = 2L;
    private final Long CATEGORY_ID = 1L;
    private final String MXIK = "MXIK1";
    private final String SEARCH = "product1";
    private final List<Long> PRODUCT_IDS = List.of(2L, 3L, 4L);


    @BeforeEach
    void setup() {
        Register register = new Register();
        register.setBranchId(BRANCH_ID);
        register.setProductId(PRODUCT_ID);
        register.setCompanyId(COMPANY_ID);
        register.setUnitId(UNIT_ID);
        register.setCityId(CITY_ID);
        register.setBalance(BigDecimal.valueOf(100));
        register.setCostPrice(BigDecimal.valueOf(10));
        register.setSalesPrice(BigDecimal.valueOf(20));
        register.setMarkingCount(50L);
        register.setCategoryId(1L);
        register.setProductName("product1");
        register.setMxik(MXIK);
        registerRepository.save(register);

        AtomicInteger mxikCount = new AtomicInteger(2);
        PRODUCT_IDS.forEach(pid -> {
            ProductBranch product = new ProductBranch();
            product.setProductId(pid);
            product.setBranchId(BRANCH_ID);
            product.setCityId(CITY_ID);
            product.setUnitId(UNIT_ID);
            product.setVatBarcode("123456");
            product.setBarcode("test" + pid);
            product.setName("product" + pid);
            product.setCategoryId(CATEGORY_ID);
            product.setCreatedBy("test123");
            product.setDeleted(false);
            productBranchRepository.save(product);

            Register reg = new Register();
            reg.setProductId(pid);
            reg.setBranchId(BRANCH_ID);
            reg.setCompanyId(COMPANY_ID);
            reg.setUnitId(UNIT_ID);
            reg.setCityId(CITY_ID);
            reg.setBalance(BigDecimal.valueOf(50));
            reg.setCostPrice(BigDecimal.valueOf(5));
            reg.setSalesPrice(BigDecimal.valueOf(10));
            reg.setProductName("product" + pid);
            reg.setCategoryId(CATEGORY_ID);
            reg.setMxik("mxik" + mxikCount);
            mxikCount.getAndIncrement();
            registerRepository.save(reg);
        });
        entityManager.flush();
        entityManager.clear();
    }

    @AfterEach
    public void cleanUp() {
        registerRepository.deleteAll();
        productBranchRepository.deleteAll();
        unitRepository.deleteAll();
        categoryBranchRepository.deleteAll();
    }

    @Test
    void findAllByFilter() {
        RegisterFilter filter = new RegisterFilter();
        filter.setCompanyId(COMPANY_ID);
        filter.setBranchIds(Set.of(BRANCH_ID));
        filter.setProductId(PRODUCT_ID);
        filter.setUnitId(UNIT_ID);
        filter.setCategoryIds(List.of(CATEGORY_ID));
        filter.setMxik(MXIK);
        filter.setSearch(SEARCH);
        filter.setOrderBy("productName");
        filter.setSortOrder(SortTypeEnum.asc);

        Page<RegisterDto> result = registerRepository.findAllByFilter(filter);
        assertEquals(1, result.getTotalElements());
        assertEquals(50, result.getContent().getLast().getMarkingCount());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.getContent().getFirst().getQty()));
    }

    @Test
    void findByProduct() {
        RegisterFilter filter = new RegisterFilter();
        filter.setCompanyId(COMPANY_ID);
        filter.setBranchIds(Set.of(BRANCH_ID));
        filter.setMxik(MXIK);
        filter.setUnitId(UNIT_ID);

        List<RegisterDto> result = registerRepository.findByProduct(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getProductId());
        assertEquals("MXIK1", result.getFirst().getMxik());
        assertEquals("product1", result.getFirst().getProductName());
        assertEquals("User Branch", result.getFirst().getBranchName());
        assertTrue(result.stream().anyMatch(registerDto -> registerDto.getUnitId().equals(2L)));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.getFirst().getQty()));
    }

    @Test
    void getStats() {
        RegisterFilter filter = new RegisterFilter();
        filter.setCompanyId(COMPANY_ID);
        filter.setBranchIds(Set.of(BRANCH_ID));
        filter.setProductId(PRODUCT_ID);
        filter.setUnitId(UNIT_ID);
        filter.setCategoryIds(List.of(CATEGORY_ID));
        filter.setMxik(MXIK);
        filter.setSearch(SEARCH);

        RegisterStatsDTO result = registerRepository.getStats(filter);

        assertNotNull(result);
        assertEquals(1, result.getProductCount());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(result.getTotalCostPrice()));
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(result.getTotalSalesPrice()));

    }

    @Test
    void findAllByBranchId() {
        Unit unit = new Unit();
        unit.setId(3L);
        unit.setName("TestUnit");
        unit.setNameUz("TestUnitUz");
        unitRepository.save(unit);

        CategoryBranch category = new CategoryBranch();
        category.setCategoryId(2L);
        category.setBranchId(BRANCH_ID);
        category.setName("TestCategory");
        categoryBranchRepository.save(category);

        Register register = new Register();
        register.setBranchId(BRANCH_ID);
        register.setProductId(5L);
        register.setCompanyId(COMPANY_ID);
        register.setUnitId(3L);
        register.setCityId(CITY_ID);
        register.setBalance(BigDecimal.valueOf(200));
        register.setProductName("testProduct");
        registerRepository.save(register);

        ProductBranch product = new ProductBranch();
        product.setProductId(5L);
        product.setBranchId(BRANCH_ID);
        product.setCityId(CITY_ID);
        product.setUnitId(3L);
        product.setBarcode("testBarcode");
        product.setVatBarcode("testVatBarcode");
        product.setName("testProduct");
        product.setSalesPrice(BigDecimal.valueOf(25));
        product.setCategoryId(2L);
        product.setDeleted(false);
        productBranchRepository.save(product);

        entityManager.flush();
        entityManager.clear();
        List<ReportDTO> result = registerRepository.findAllByBranchIdAndCityId(BRANCH_ID, CITY_ID);

        assertNotNull(result);
        assertEquals(1, result.size());

        ReportDTO reportDTO = result.getFirst();
        assertEquals("TestCategory", reportDTO.getCategoryName());
        assertEquals("testProduct", reportDTO.getProductName());
        assertEquals("testBarcode", reportDTO.getBarcode());
        assertEquals("testVatBarcode", reportDTO.getVatBarcode());
        assertEquals("TestUnit", reportDTO.getUnitName());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(reportDTO.getSalesPrice()));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(reportDTO.getBalance()));
    }

    @Test
    void deleteByBranchIdAndProductId() {
        registerRepository.deleteByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        Optional<Register> result = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(result.isEmpty());
    }


    @Test
    void findOneByBranchIdAndProductId() {
        Optional<Register> result = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(result.isPresent());
        assertEquals(BRANCH_ID, result.get().getBranchId());
        assertEquals(PRODUCT_ID, result.get().getProductId());
    }

    @Test
    void findAllByBranchIdAndProductIdIn() {
        List<Register> result = registerRepository.findAllByBranchIdAndProductIdIn(BRANCH_ID, PRODUCT_IDS);
        assertEquals(PRODUCT_IDS.size(), result.size());
        result.forEach(register -> {
            assertEquals(BRANCH_ID, register.getBranchId());
            assertTrue(PRODUCT_IDS.contains(register.getProductId()));
        });
    }

    @Test
    void resetWarehouse() {
        registerRepository.resetWarehouse(BRANCH_ID);
        Optional<Register> register = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(register.isPresent());
        assertEquals(0, BigDecimal.valueOf(0).compareTo(register.get().getBalance()));
        assertEquals(0, register.get().getMarkingCount());
    }

    @Test
    void deleteByBranchId() {
        Optional<Register> result = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(result.isPresent());
        registerRepository.deleteByBranchId(BRANCH_ID);
        Optional<Register> result1 = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(result1.isEmpty());
    }

    @Test
    void deleteAllByBranchIdAndCityIdAndProductIdIn() {
        registerRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(BRANCH_ID, CITY_ID, PRODUCT_IDS);
        List<Register> registers = registerRepository.findAllByBranchIdAndProductIdIn(BRANCH_ID, PRODUCT_IDS);
        assertEquals(0, registers.size());
    }

    @Test
    void cleanup() {
        ProductBranch productForDelete = new ProductBranch();
        productForDelete.setProductId(PRODUCT_ID);
        productForDelete.setBranchId(BRANCH_ID);
        productForDelete.setCityId(CITY_ID);
        productForDelete.setUnitId(UNIT_ID);
        productForDelete.setVatBarcode("123456");
        productForDelete.setBarcode("test1");
        productForDelete.setName("product1");
        productForDelete.setCategoryId(CATEGORY_ID);
        productForDelete.setCreatedBy("test123");
        productForDelete.setDeleted(true);
        productBranchRepository.save(productForDelete);

        registerRepository.cleanup(CITY_ID);
        Optional<Register> result = registerRepository.findOneByBranchIdAndProductId(BRANCH_ID, PRODUCT_ID);
        assertTrue(result.isEmpty());
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