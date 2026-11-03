package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.Reference;

import java.util.Optional;

public interface ReferenceRepository extends JpaRepository<Reference, Long>, JpaSpecificationExecutor<Reference> {
    Optional<Reference> findByCode(String code);
}