package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.silen.domain.GivePermission;

public interface GivePermissionRepository extends JpaRepository<GivePermission, String> {

    Page<GivePermission> findAllByChildId(String childOrganizationId, Pageable pageable);

    @Query("select coalesce(sum(quantity), 0) from GivePermission where childId=:childId")
    Long sumQuantityAllByChildId(@Param("childId") String childOrganizationId);
}
