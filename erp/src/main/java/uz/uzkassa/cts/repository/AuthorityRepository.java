package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.cts.domain.permission.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Authority findByName(String name);
}
