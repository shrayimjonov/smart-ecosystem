package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the TelegramAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelegramAccountRepository extends JpaRepository<TelegramAccount, Long>, TelegramAccountRepositoryCustom {
    List<TelegramAccount> findAllByPhone(String telegramLogin);

    TelegramAccount findByPhone(String phoneNumber);

    Optional<TelegramAccount> findFirstByPhoneAndTelegramIdIsNotNull(String phoneNumber);

    List<TelegramAccount> findAllByNameOrId(String username, Long id);

    @Query("select t from TelegramAccount t where t.enabled = true and t.companyId = ?1 ")
    List<TelegramAccount> findAllbyCompanyIdAndEnabledIsTrue(@Param("companyId") Long companyId);

    @Query("select distinct t.agreementId from TelegramAccount t where t.enabled = true ")
    List<Long> findAllAgreementIdIsEnabled();

    @Modifying
    @Query(value = "update TelegramAccount set enabled=false where agreementId in (:agreementIds)")
    void disableByAgreementIds(@Param("agreementIds") List<Long> agreementIds);

    @Modifying
    @Query(value = "update TelegramAccount set enabled=true where agreementId in (:agreementIds) and enabled=false")
    void enableByAgreementIds(@Param("agreementIds") List<Long> activeAgreementIds);

    List<TelegramAccount> findAllByTelegramId(Long telegramId);

    boolean existsByCompanyId(Long companyId);

    boolean existsByPhoneAndTelegramIdNotNull(String phone);
}
