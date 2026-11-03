package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.PrintTemplate;

import java.util.Optional;

public interface PrintTemplateRepository extends JpaRepository<PrintTemplate, String>, PrintTemplateRepositoryCustom {

    Optional<PrintTemplate> findFirstByOrganizationIdAndProductIdAndNameIgnoreCase(String organizationId, String productId, String name);
}
