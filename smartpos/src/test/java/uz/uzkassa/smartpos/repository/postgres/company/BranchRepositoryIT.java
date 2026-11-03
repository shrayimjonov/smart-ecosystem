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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.config.TestDatabaseConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.dto.AdminBranchDTO;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListMapDTO;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.filter.BranchFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class BranchRepositoryIT {
    @Autowired
    BranchRepository branchRepository;

    @Test
    void lookup() {
        QueryCountHolder.clear();
        BranchFilter filter = new BranchFilter();
        filter.setCompanyId(2L);
        List<BranchSelectItem> branches = branchRepository.lookup(filter);
        assertEquals(1, branches.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAllByFilter() {
        QueryCountHolder.clear();
        BranchFilter filter = Instancio.create(BranchFilter.class);
        filter.setOrderBy(null);
        Page<BranchListDTO> branches = branchRepository.findAllByFilter(filter);
        assertEquals(0, branches.getTotalElements());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void getBranchesForMap() {
        QueryCountHolder.clear();
        CompanyFilter filter = Instancio.create(CompanyFilter.class);
        List<BranchListMapDTO> branches = branchRepository.getBranchesForMap(filter);
        assertEquals(0, branches.size());
        assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
    }

    @Test
    void findAdminBranches() {
        QueryCountHolder.clear();
        CompanyFilter filter = Instancio.create(CompanyFilter.class);
        filter.setOrderBy(null);
        Page<AdminBranchDTO> adminBranches = branchRepository.findAdminBranches(filter, false);
        assertEquals(0, adminBranches.getTotalElements());
        assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
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