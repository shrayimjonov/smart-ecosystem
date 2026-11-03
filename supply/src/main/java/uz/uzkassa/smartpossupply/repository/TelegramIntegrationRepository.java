package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;

import java.util.Optional;

@Repository
public interface TelegramIntegrationRepository extends BaseRepository<TelegramIntegration, Long>, TelegramIntegrationRepositoryCustom {

    @EntityGraph(attributePaths = {"company"})
    Optional<TelegramIntegration> findFirstByBotToken(String botToken);

    @Query("select ti.companyId from TelegramIntegration ti where ti.botToken=:botToken")
    Long getCompanyIdByBotToken(@Param("botToken") String botToken);
}
