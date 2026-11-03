package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingCompanyItem;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeCountByRoleDTO;
import uz.uzkassa.smartpossupply.filters.UserFilter;

import java.util.List;

@Repository
public interface UserRepositoryCustom {
    ResultList<User> getResultList(UserFilter filter);

    List<User> lookUp(UserFilter filter);

    CommonStatisticsDTO getStatistics(UserFilter filter);

    List<EmployeeCountByRoleDTO> getCountByRole(String tin, Long companyId);

    List<BillingCompanyItem> getCompanyItems(String tin, Long companyId);
}
