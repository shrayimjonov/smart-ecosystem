package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.OrganizationPermissions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrganizationPermissionRepository extends JpaRepository<OrganizationPermissions, String>, CacheConstants, OrganizationPermissionRepositoryCustom {

    List<OrganizationPermissions> findAllByPermissionsKeyAndExpireDateBeforeAndCountGreaterThan(String permissionsKey, LocalDateTime expireDate, int count);

    @Query("select coalesce(sum(p.count), 0) < :qty from OrganizationPermissions p where p.orgId = :organizationId and p.permissionsKey=:permissionsKey")
    boolean checkByOrganizationIdAndPermissionsKeyAndCountLessThan(@Param("organizationId") String organizationId, @Param("permissionsKey") String permissionsKey, @Param("qty") Long qty);

    @Query("select o.permissionsKey from OrganizationPermissions o where o.orgId=:organizationId and o.expireDate > current_timestamp ")
    Set<String> findAllPermissionKeysByOrganizationIdAndExpireDateAfter(@Param("organizationId") String organizationId);

    Optional<OrganizationPermissions> findFirstByOrganizationIdAndPermissionsKeyEquals(String organizationId, String permissionsKey);

    List<OrganizationPermissions> findAllByOrgIdAndPermissionsKeyIn(String organizationId, List<String> permissionsKeys);

    Optional<OrganizationPermissions> findFirstByOrganizationIdAndPermissionsKeyEqualsAndCountGreaterThan(String organizationId, String permissionsKey, int count);
}
