package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TelegramUsernames;

import java.util.Optional;

@Repository
public interface TelegramUsernameRepository extends BaseRepository<TelegramUsernames, Long> {

    @Modifying
    @Query(value =
            """
                    insert into telegram_usernames (employee_id, username)
                    values (:employeeId, :username)
                    on conflict (employee_id)
                        do update set username    = excluded.username ,
                                      employee_id = excluded.employee_id\s
                    """, nativeQuery = true)
    void insertUpdate(@Param("employeeId") Long employeeId, @Param("username") String username);

    Optional<TelegramUsernames> findByEmployeeId(Long employeeId);
}
