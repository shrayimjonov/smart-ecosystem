package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.discount.Discount;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Repository
public interface DiscountRepositoryCustom {

    ResultList<Discount> getResultList(BaseFilter filter);

    List<Discount> lookUp(BaseFilter filter);
}
