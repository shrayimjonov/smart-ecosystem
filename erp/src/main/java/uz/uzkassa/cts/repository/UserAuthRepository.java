package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.UserAuth;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends BaseRepository<UserAuth, Long> {

    Optional<UserAuth> findFirstByUsername(String username);

    Optional<UserAuth> findFirstByUsernameAndActivationCodeOrderByIdDesc(String username, String activationCode);

    boolean existsByUsernameAndSecretKey(String username, String secretKey);

    boolean existsByUsernameAndActivationCode(String username, String activationCode);

    void deleteAllByUsername(String username);

}
