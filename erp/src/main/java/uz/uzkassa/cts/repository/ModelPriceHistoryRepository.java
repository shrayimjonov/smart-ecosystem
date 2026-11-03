package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ModelPriceHistory;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.OrderType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelPriceHistoryRepository extends BaseRepository<ModelPriceHistory, Long> {

    ModelPriceHistory findTopByModelIdOrderByIdDesc(Long modelId);

    List<ModelPriceHistory> findAllByOrderTypeAndCompanyType(OrderType orderType, CompanyType companyType);

    Optional<ModelPriceHistory> findFirstByOrderTypeAndCompanyTypeAndActiveIsTrueOrderByIdDesc(OrderType orderType, CompanyType companyType);
}
