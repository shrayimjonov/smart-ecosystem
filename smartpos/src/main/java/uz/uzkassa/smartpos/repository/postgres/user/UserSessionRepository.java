package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.UserSession;
import uz.uzkassa.smartpos.enums.ShiftStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    @Modifying
    @Query("update UserSession s set s.endDateTime = :endOfTheDay, s.status = 'CLOSED' " +
        "where s.startDateTime < :endOfTheDay and s.status = 'OPEN'")
    void closeAllOpenSessions(@Param("endOfTheDay") LocalDateTime endOfTheDay);

    Optional<UserSession> findFirstByUserIdAndStatusOrderByStartDateTimeDesc(Long userId, ShiftStatus status);

    Optional<UserSession> findFirstByTerminalIdAndStatusOrderByStartDateTimeDesc(Long terminalId, ShiftStatus status);
}
