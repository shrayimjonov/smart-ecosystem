package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.OperatorPermission;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OperatorPermissionRepository extends JpaRepository<OperatorPermission, Long>, JpaSpecificationExecutor<OperatorPermission> {

    OperatorPermission findByCompanyIdAndUser_Login(Long companyId, String login);

    OperatorPermission findByCompanyIdAndExpireDateAfterAndUser_LoginAndActivatedIsTrue(Long companyId, LocalDateTime date, String login);

    Optional<OperatorPermission> findTopByCompanyIdAndLastModifiedDateIsNotNullOrderByLastModifiedDateDesc(Long companyId);

    Optional<OperatorPermission> findFirstByCompanyIdAndUserId(Long companyId, Long userId);
}
