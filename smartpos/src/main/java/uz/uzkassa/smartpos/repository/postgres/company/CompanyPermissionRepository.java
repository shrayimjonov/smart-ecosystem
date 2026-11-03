package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyPermission;

import java.util.List;

public interface CompanyPermissionRepository extends JpaRepository<CompanyPermission, Long> {
    List<CompanyPermission> findAllByCompanyId(Long companyId);
}