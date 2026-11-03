package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.catalog.ProductListRecord;
import uz.uzkassa.smartpos.dto.catalog.ProductLookupRecord;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.filter.ProductBranchFilter;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import(TestAuditorAwareConfig.class)
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class ProductBranchRepositoryIT {
    private static final Long BRANCH_ID = 2L;
    private static final Long CITY_ID = 9L;

    @Autowired
    ProductBranchRepository productBranchRepository;

    @AfterEach
    void cleanUp() {
        productBranchRepository.deleteAll();
    }

    @Test
    void pageCabinet() {
        var productBranch = new ProductBranch();
        productBranch.setProductId(1L);
        productBranch.setCategoryId(1L);
        productBranch.setBranchId(BRANCH_ID);
        productBranch.setCityId(CITY_ID);
        productBranch.setCategoryId(1L);
        productBranch.setFavorite(true);
        productBranch.setAdvanceEnabled(true);
        productBranch.setCreditEnabled(true);
        productBranch.setName("Equal names only differ by attribute values");
        productBranch.setBarcode(RandomStringUtils.randomNumeric(14));
        productBranch.setVatBarcode(RandomStringUtils.randomNumeric(17));
        productBranchRepository.saveAndFlush(productBranch);

        productBranch = new ProductBranch();
        productBranch.setProductId(2L);
        productBranch.setCategoryId(1L);
        productBranch.setBranchId(BRANCH_ID);
        productBranch.setCityId(CITY_ID);
        productBranch.setCategoryId(1L);
        productBranch.setFavorite(true);
        productBranch.setAdvanceEnabled(true);
        productBranch.setCreditEnabled(true);
        productBranch.setName("Equal names only differ by attribute values");
        productBranch.setBarcode(RandomStringUtils.randomNumeric(14));
        productBranch.setVatBarcode(RandomStringUtils.randomNumeric(17));
        AttributeValue attributeValue = Instancio.create(AttributeValue.class);
        AttributeValue attributeValue1 = Instancio.create(AttributeValue.class);
        productBranch.setAttributes(new HashSet<>(List.of(attributeValue, attributeValue1)));
        productBranchRepository.saveAndFlush(productBranch);

        ProductBranchFilter filter = new ProductBranchFilter();
        filter.setBranchId(BRANCH_ID);
        filter.setCityId(CITY_ID);
        filter.setCategoryId(1L);
        filter.setCategoryIds(List.of(1L));
        filter.setOnlyFavorites(true);
        filter.setAdvanceEnabled(true);
        filter.setCreditEnabled(true);
        filter.setSearch(productBranch.getName().substring(0, 24));
        filter.setAttributes(List.of(attributeValue.getKey() + ":" + attributeValue.getCode(), attributeValue1.getKey() + ":" + attributeValue1.getCode()));

        Page<ProductListRecord> result = productBranchRepository.pageCabinet(filter);

        assertEquals(1, result.getNumberOfElements());
        assertEquals(productBranch.getProductId(), result.getContent().getFirst().productId());
        assertEquals(productBranch.getName(), result.getContent().getFirst().name());
        assertEquals(productBranch.getBarcode(), result.getContent().getFirst().barcode());
        assertEquals(productBranch.getVatBarcode(), result.getContent().getFirst().vatBarcode());
        assertEquals(productBranch.isHasMark(), result.getContent().getFirst().hasMark());
        assertEquals(productBranch.getSalesPrice(), result.getContent().getFirst().salesPrice());
    }

    @Test
    void lookup() {
        var productBranch = new ProductBranch();
        productBranch.setProductId(1L);
        productBranch.setCategoryId(1L);
        productBranch.setBranchId(BRANCH_ID);
        productBranch.setCityId(CITY_ID);
        productBranch.setCategoryId(1L);
        productBranch.setFavorite(true);
        productBranch.setAdvanceEnabled(true);
        productBranch.setCreditEnabled(true);
        productBranch.setName(RandomStringUtils.randomAlphabetic(25));
        productBranch.setBarcode(RandomStringUtils.randomNumeric(14));
        productBranch.setVatBarcode(RandomStringUtils.randomNumeric(17));
        productBranchRepository.saveAndFlush(productBranch);

        productBranch = new ProductBranch();
        productBranch.setProductId(2L);
        productBranch.setCategoryId(1L);
        productBranch.setBranchId(BRANCH_ID);
        productBranch.setCityId(CITY_ID);
        productBranch.setCategoryId(1L);
        productBranch.setFavorite(true);
        productBranch.setAdvanceEnabled(true);
        productBranch.setCreditEnabled(true);
        productBranch.setName(RandomStringUtils.randomAlphabetic(25));
        productBranch.setBarcode(RandomStringUtils.randomNumeric(14));
        productBranch.setVatBarcode(RandomStringUtils.randomNumeric(17));
        productBranchRepository.saveAndFlush(productBranch);

        ProductBranchFilter filter = new ProductBranchFilter();
        filter.setBranchId(BRANCH_ID);
        filter.setCityId(CITY_ID);
        filter.setCategoryId(1L);
        filter.setOnlyFavorites(true);
        filter.setAdvanceEnabled(true);
        filter.setCreditEnabled(true);
        filter.setSearch(productBranch.getName().substring(0, 24));

        List<ProductLookupRecord> result = productBranchRepository.lookup(filter);

        assertEquals(1, result.size());
        assertEquals(productBranch.getProductId(), result.getFirst().id());
        assertEquals(BRANCH_ID, result.getFirst().branchId());
        assertEquals(productBranch.getName(), result.getFirst().name());
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