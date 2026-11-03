package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;

import java.util.List;

@Repository
public interface ExpeditorRepository extends BaseRepository<User, Long>, ExpeditorRepositoryCustom {
    List<User> findAllByRoleAndStatusAndDeletedIsFalse(Role role, UserStatus userStatus);
}
