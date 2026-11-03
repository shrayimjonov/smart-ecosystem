package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.auth.UserSession;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 18:07
 */
@Repository
public interface UserSessionRepository extends BaseRepository<UserSession, Long> {
    Optional<UserSession> findFirstByUserIdAndStatusOrderByCreatedDateDesc(Long userId, ShiftStatus status);
}
