package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.company.OneCCompany;

public interface OneCCompanyRepository extends JpaRepository<OneCCompany, Long>, JpaSpecificationExecutor<OneCCompany> {
}
