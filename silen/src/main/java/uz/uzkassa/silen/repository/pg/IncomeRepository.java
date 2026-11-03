package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Income;

/**
 * Spring Data repository for the Income entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>, IncomeRepositoryCustom {
    @Query("select max(intNumber) from Income where organizationId=?1")
    Integer getMaxIntNumber(String organizationId);

    boolean existsShipmentByOrganizationIdAndNumberIgnoreCase(String organizationId, String number);
}
