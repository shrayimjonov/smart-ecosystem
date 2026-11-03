package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptTemplate;

import java.util.Optional;


public interface ReceiptTemplateRepository extends JpaRepository<ReceiptTemplate, Long> {
    Optional<ReceiptTemplate> findDistinctTopByCompanyId(Long companyId);
}
