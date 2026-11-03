package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.dashboard.OrganizationsDTO;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;

import java.util.List;

public interface OrganizationRepositoryCustom {
    /**
     * Filter by {@link OrganizationFilter organizationFilter} for
     * {@link uz.uzkassa.silen.web.rest.OrganizationResource OrganizationResource }
     * Filter by {@link uz.uzkassa.silen.dto.dashboard.DashboardFilter dashboardFilter} for
     * {@link uz.uzkassa.silen.web.rest.DashboardResource DashboardResource}
     *
     * @param filter
     * @return Page<Organization>
     */
    Page<Organization> findAllByFilter(OrganizationFilter filter);

    List<OrganizationsDTO> findOrganizations();

    void createSequence(String gcp);
}
