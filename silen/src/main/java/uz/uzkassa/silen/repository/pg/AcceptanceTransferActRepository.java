package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.AcceptanceTransferAct;

import java.util.Optional;

@Repository
public interface AcceptanceTransferActRepository extends JpaRepository<AcceptanceTransferAct, String>, AcceptanceTransferActRepositoryCustom {

    Optional<AcceptanceTransferAct> findFirstByOrganizationIdAndActNumber(String organizationId, String actNumber);

    boolean existsByOrganizationIdAndActNumberAndIdNot(String organizationId, String actNumber, String id);
}
