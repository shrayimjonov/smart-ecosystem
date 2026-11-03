package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.Category;
import uz.uzkassa.smartpos.enums.CompanyType;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByCompanyType(CompanyType companyType);
}
