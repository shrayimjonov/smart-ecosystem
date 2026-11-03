package uz.uzkassa.smartpos.repository.postgres.company;

import net.ttddyy.dsproxy.QueryCountHolder;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyPermission;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CurrentBranchesAndTerminalsDTO;
import uz.uzkassa.smartpos.dto.filter.CompanySpecificationFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class CompanyRepositoryIT {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private CompanyPermissionRepository companyPermissionRepository;

    @Test
    void search() {
        QueryCountHolder.clear();
        List<CommonDTO> clients = companyRepository.search("User");
        assertEquals(1, clients.size());
        assertEquals(2, clients.getFirst().getId());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void getCurrentBranchesAndTerminalsCountByCompany() {
        QueryCountHolder.clear();
        jdbcClient.sql("update branch set registered = true where id = 2").update();
        CurrentBranchesAndTerminalsDTO dto = companyRepository.getCurrentBranchesAndTerminalsCountByCompany(2L);
        assertEquals(1, dto.getBranchCount());
        assertEquals(0, dto.getTerminalCount());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
    }

    @Test
    void expiredCompaniesWhenBillingTypeIsNotSmartpos() {
        QueryCountHolder.clear();
        List<CommonDTO> expiredCompanies = companyRepository.expiredCompanies();
        assertEquals(0, expiredCompanies.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void expiredCompaniesWhenBillingTypeIsSmartpos() {
        QueryCountHolder.clear();
        companyRepository.findById(2L)
                .ifPresent(company -> {
                    company.setBillingType(BillingType.SMARTPOS);
                    companyRepository.saveAndFlush(company);
                });
        List<CommonDTO> expiredCompanies = companyRepository.expiredCompanies();
        assertEquals(1, expiredCompanies.size());
        var first = expiredCompanies.getFirst();
        assertEquals(2L, first.getId());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
    }

    @Test
    void expiredCompaniesWhenFreeTrialIsTrue() {
        QueryCountHolder.clear();
        jdbcClient.sql("update company set free_trial = true where id = 2").update();
        List<CommonDTO> expiredCompanies = companyRepository.expiredCompanies();
        assertEquals(0, expiredCompanies.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
    }

    @Test
    void expiredCompanies() {
        QueryCountHolder.clear();
        companyRepository.findById(2L)
                .ifPresent(company -> {
                    company.setBillingType(BillingType.SMARTPOS);
                    companyRepository.saveAndFlush(company);
                });
        CompanyPermission companyPermission = new CompanyPermission();
        companyPermission.setCompanyId(2L);
        companyPermission.setEndDate(LocalDateTime.now().minusDays(1));
        companyPermission.setServiceType(ServiceType.WAREHOUSE);
        companyPermission.setXizmatType(XizmatType.GROUPED);
        companyPermissionRepository.saveAndFlush(companyPermission);
        List<CommonDTO> expiredCompanies = companyRepository.expiredCompanies();
        assertEquals(1, expiredCompanies.size());
        var first = expiredCompanies.getFirst();
        assertEquals(2L, first.getId());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
        assertEquals(1, QueryCountHolder.getGrandTotal().getInsert());
    }

    @Test
    void companyLookup() {
        QueryCountHolder.clear();
        var filter = Instancio.create(CompanySpecificationFilter.class);
        List<CompanyBaseDTO> companies = companyRepository.companyLookup(filter);
        assertTrue(companies.isEmpty());
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