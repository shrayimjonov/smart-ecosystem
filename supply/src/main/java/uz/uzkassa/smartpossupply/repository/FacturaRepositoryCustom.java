package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsByStatusDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.FacturaStatisticsFilter;

import java.util.List;

@Repository
public interface FacturaRepositoryCustom {
    ResultList<Factura> getResultList(FacturaFilter filter);

    List<Factura> lookUp(FacturaFilter filter);

    ResultList<Factura> getListTo1C(FacturaFilter filter);

    List<FacturaStatisticsDTO> getFacturaStatistics(FacturaStatisticsFilter filter);

    FacturaStatisticsByStatusDTO getStatistics(FacturaFilter filter);

    Long getMonthStatistics(BaseFilter filter);
}
