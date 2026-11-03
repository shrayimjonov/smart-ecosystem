package uz.uzkassa.smartpos.repository.postgres.catalog;

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
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.catalog.Category;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.catalog.CategoryResult;
import uz.uzkassa.smartpos.dto.catalog.CategoryTreeDto;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.enums.CompanyType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import(TestAuditorAwareConfig.class)
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class CategoryBranchRepositoryIT {
    private static final Long COMPANY_ID = 2L;
    private static final Long BRANCH_ID = 2L;
    private static final Long CITY_ID = 9L;
    @Autowired
    CategoryBranchRepository categoryBranchRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductBranchRepository productBranchRepository;
    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        var list = new ArrayList<CategoryBranch>();
        var products = new ArrayList<ProductBranch>();
        for (int i = 0; i < 10; i++) {
            var category = new CategoryBranch();
            category.setBranchId(BRANCH_ID);
            category.setCategoryId(categoryBranchRepository.nextCategoryId());
            category.setName("Category " + i);
            list.add(category);

            var productBranch = new ProductBranch();
            productBranch.setProductId(i + 1L);
            productBranch.setCategoryId(1L);
            productBranch.setBranchId(BRANCH_ID);
            productBranch.setCityId(CITY_ID);
            productBranch.setCategoryId(category.getCategoryId());
            productBranch.setFavorite(true);
            productBranch.setAdvanceEnabled(true);
            productBranch.setCreditEnabled(true);
            productBranch.setName(RandomStringUtils.randomAlphabetic(15));
            productBranch.setBarcode(RandomStringUtils.randomNumeric(14));
            productBranch.setVatBarcode(RandomStringUtils.randomNumeric(17));
            products.add(productBranch);
        }
        categoryBranchRepository.saveAllAndFlush(list);
        productBranchRepository.saveAllAndFlush(products);
    }

    @AfterEach
    void tearDown() {
        categoryBranchRepository.deleteAll();
    }

    @Test
    void nextCategoryId() {
        QueryCountHolder.clear();
        Long nextId = categoryBranchRepository.nextCategoryId();
        assertNotNull(nextId);
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void enableDisableCategory() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
        assertFalse(categories.isEmpty());

        var size = categories.size();
        var first = categories.getFirst();
        categoryBranchRepository.enableDisableCategory(false, first.getCategoryId(), BRANCH_ID);
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());

        QueryCountHolder.clear();
        categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertEquals(size - 1, categories.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());


        QueryCountHolder.clear();
        Optional<CategoryBranch> disabled = categoryBranchRepository.findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(first.getCategoryId(), BRANCH_ID);
        assertTrue(disabled.isEmpty());
        categoryBranchRepository.enableDisableCategory(true, first.getCategoryId(), BRANCH_ID);
        disabled = categoryBranchRepository.findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(first.getCategoryId(), BRANCH_ID);
        assertTrue(disabled.isPresent());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
    }

    @Test
    void findAllByBranchIdAndEnabledIsTrue() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        assertEquals(10, categories.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void existsByBranchIdAndLastModifiedDateAfter() {
        QueryCountHolder.clear();
        var now = LocalDateTime.now();
        CategoryBranch categoryBranch = new CategoryBranch();
        categoryBranch.setBranchId(BRANCH_ID);
        categoryBranch.setCategoryId(categoryBranchRepository.nextCategoryId());
        categoryBranch.setLastModifiedDate(now);
        categoryBranchRepository.save(categoryBranch);
        assertTrue(categoryBranchRepository.existsByBranchIdAndLastModifiedDateAfter(BRANCH_ID, now));
        assertEquals(3, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getInsert());
    }

    @Test
    void findAllByEnabledIsTrueAndBranchIdAndNameIn() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByEnabledIsTrueAndBranchIdAndNameIn(BRANCH_ID, List.of("Category 1", "Category 2"));
        assertFalse(categories.isEmpty());
        assertEquals(2, categories.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findFirstByCategoryIdAndBranchIdAndEnabledIsTrue() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        var first = categories.getFirst();
        Optional<CategoryBranch> category = categoryBranchRepository.findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(first.getCategoryId(), BRANCH_ID);
        assertTrue(category.isPresent());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findFirstByBranch_IdAndNameAndEnabledIsTrue() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        var first = categories.getFirst();
        Optional<CategoryBranch> category = categoryBranchRepository.findFirstByBranch_IdAndNameAndEnabledIsTrue(BRANCH_ID, first.getName());
        assertTrue(category.isPresent());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findCategoryIdsByBranch() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        List<Long> ids = categoryBranchRepository.findCategoryIdsByBranch(BRANCH_ID);
        assertFalse(ids.isEmpty());
        assertEquals(categories.size(), ids.size());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findBranchCategories() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findBranchCategories(BRANCH_ID);
        assertFalse(categories.isEmpty());
        assertEquals(10, categories.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void deleteByBranchId() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        categoryBranchRepository.deleteByBranchId(BRANCH_ID);
        categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertTrue(categories.isEmpty());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getDelete());
    }

    @Test
    void disableCategoryBranches() {
        QueryCountHolder.clear();
        List<CategoryBranch> categories = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(BRANCH_ID);
        assertFalse(categories.isEmpty());
        var first = categories.getFirst();
        categoryBranchRepository.disableCategoryBranches("disable-test", BRANCH_ID, List.of(categories.getFirst().getCategoryId()));
        Optional<String> optional = jdbcClient.sql("select last_modified_by from category_branch where branch_id = ? and category_id = ?")
                .param(BRANCH_ID)
                .param(first.getCategoryId())
                .query(String.class)
                .optional();
        assertTrue(optional.isPresent());
        assertEquals("disable-test", optional.get());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
    }

    @Test
    void createFashionCatalog() {
        QueryCountHolder.clear();
        Category category = new Category();
        category.setCompanyType(CompanyType.FASHION);
        category.setName(UUID.randomUUID().toString());
        categoryRepository.save(category);
        categoryBranchRepository.createFashionCatalog(BRANCH_ID);
        List<CategoryBranch> list = categoryBranchRepository.findAllByEnabledIsTrueAndBranchIdAndNameIn(BRANCH_ID, List.of(category.getName()));
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(2, QueryCountHolder.getGrandTotal().getInsert());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllWithoutProductsCount() {
        QueryCountHolder.clear();
        List<CategoryTreeDto> categories = categoryBranchRepository.findAllWithoutProductsCount(BRANCH_ID);
        assertFalse(categories.isEmpty());
        assertEquals(10, categories.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findEnabledCategories() {
        QueryCountHolder.clear();
        List<CategoryDTO> categories = categoryBranchRepository.findEnabledCategories(BRANCH_ID, CITY_ID);
        assertFalse(categories.isEmpty());
        assertEquals(10, categories.size());
        categories.forEach(categoryDTO -> {
            assertEquals(1, categoryDTO.getProductCount());
            assertEquals(1, categoryDTO.getAdvanceCount());
            assertEquals(1, categoryDTO.getCreditCount());
        });
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void choose() {
        QueryCountHolder.clear();
        BaseFilter filter = new BaseFilter();
        filter.setCompanyId(COMPANY_ID);
        filter.setSearch("Category 1");
        List<CommonDTO> categories = categoryBranchRepository.choose(filter);
        assertFalse(categories.isEmpty());
        assertEquals(1, categories.size());
        assertEquals("Category 1", categories.getFirst().getName());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAll() {
        List<CategoryResult> categories = categoryBranchRepository.findAll(BRANCH_ID, CITY_ID);
        assertEquals(10, categories.size());
        categories.forEach(result -> {
            assertEquals(1, result.productCount());
            assertEquals(1, result.advanceCount());
            assertEquals(1, result.creditCount());
        });
    }


    @BeforeAll
    static void setUpContainer() {
        postgreSQL.start();
    }

    @AfterAll
    static void tearDownContainer() {
        postgreSQL.stop();
    }

    public static final CustomPostgreSQLContainer postgreSQL = CustomPostgreSQLContainer.getInstance();
}