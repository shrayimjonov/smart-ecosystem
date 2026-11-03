package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.auth.UserAuth;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.02.2024
 */
@Repository
public interface UserAuthRepository extends BaseRepository<UserAuth, Long> {

    Optional<UserAuth> findFirstByUsername(String username);

    Optional<UserAuth> findFirstByUsernameAndSecretKey(String username, String secretKey);

}
