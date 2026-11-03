package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    @Query(value = "select p.code from Permission p")
    List<String> findAllPermissionCodes();
}
