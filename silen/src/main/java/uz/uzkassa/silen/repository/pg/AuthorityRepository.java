package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Permission;

/**
 * Spring Data JPA repository for the {@link Permission} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Permission, String> {
}
