package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.Advertisement;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement>, AdvertisementRepositoryCustom {
}