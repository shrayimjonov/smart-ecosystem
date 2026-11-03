package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Store;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Store entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    Optional<Store> findFirstByNameAndOrganizationIdAndDeletedIsFalse(String name, String organizationId);

    Optional<Store> findFirstByOrganizationIdAndDeletedIsFalseAndMainIsTrue(String organizationId);

    List<Store> findAllByOrganizationIdAndDeletedIsFalseAndMainIsFalse(String organizationId);
}
