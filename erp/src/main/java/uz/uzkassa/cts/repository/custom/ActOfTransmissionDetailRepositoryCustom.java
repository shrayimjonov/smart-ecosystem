package uz.uzkassa.cts.repository.custom;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmissionDetail;
import uz.uzkassa.cts.filter.ActOfTransmissionDetailFilter;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ActOfTransmissionDetailRepositoryCustom {

    Specification<ActOfTransmissionDetail> getSpecification(ActOfTransmissionDetailFilter filter);

    BigDecimal getTotalIncome(ActOfTransmissionDetailFilter filter);

    List<Object[]> resultDebtors();
}
