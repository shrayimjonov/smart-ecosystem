package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.UzcardConnection;
import uz.uzkassa.cts.repository.custom.UzcardConnectionRepositoryCustom;

@Repository
public interface UzcardConnectionRepository extends BaseRepository<UzcardConnection, Long>, UzcardConnectionRepositoryCustom {
}
