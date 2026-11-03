package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.XtraceToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface XtraceTokenRepository extends BaseRepository<XtraceToken, Long> {

    Optional<XtraceToken> findByCompanyId(Long companyId);

    @Cacheable(cacheNames = XTRACE_TOKEN_BY_COMPANY_ID_CACHE)
    @Query("select xt.apiKey from XtraceToken xt where xt.companyId=:companyId")
    String getToken(@Param("companyId") Long companyId);

    List<XtraceToken> findAllByExpireDateBefore(LocalDateTime date);

    List<XtraceToken> findAllByExpireDateBetween(LocalDateTime start, LocalDateTime end);

    List<XtraceToken> findAllByApiKeyNotNull();
}
