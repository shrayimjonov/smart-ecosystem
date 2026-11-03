package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.AccessoryModel;
import uz.uzkassa.cts.repository.custom.AccessoryModelRepositoryCustom;

@Repository
public interface AccessoryModelRepository extends BaseRepository<AccessoryModel, Long>, AccessoryModelRepositoryCustom {
}
