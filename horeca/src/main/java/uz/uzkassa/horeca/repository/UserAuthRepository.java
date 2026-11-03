package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.auth.UserAuth;

import java.util.Optional;

public interface UserAuthRepository extends BaseRepository<UserAuth, Long> {
    Optional<UserAuth> findFirstByLoginAndActivationCodeOrderByIdDesc(String login, String activationCode);

    Optional<UserAuth> findFirstByRefreshTokenOrderByIdDesc(String refreshToken);

    boolean existsByLoginAndSecretKey(String login, String secretKey);

    void deleteAllByLogin(String login);
}
