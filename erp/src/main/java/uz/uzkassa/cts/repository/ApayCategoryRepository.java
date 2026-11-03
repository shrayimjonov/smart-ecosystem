package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.repository.custom.ApayCategoryRepositoryCustom;

import java.util.Optional;

@Repository
public interface ApayCategoryRepository extends BaseRepository<APayCategory, Long>, ApayCategoryRepositoryCustom {

    Optional<APayCategory> findFirstByApayId(String aPayId);
}
