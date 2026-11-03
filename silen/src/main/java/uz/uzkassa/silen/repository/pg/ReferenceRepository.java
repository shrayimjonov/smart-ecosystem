package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Reference;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Reference entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReferenceRepository extends JpaRepository<Reference, String>, ReferenceRepositoryCustom {
    Optional<Reference> findFirstByParentIdAndCodeAndNameAndDeletedIsFalse(String parentId, String code, String name);
    List<Reference> findByCode(String code);
}
