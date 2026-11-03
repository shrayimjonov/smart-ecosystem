package uz.uzkassa.smartpos.repository.clickhouse.sales;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.clickhouse.SalesDetailClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailDynamicsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopProductDto;
import uz.uzkassa.smartpos.filter.SalesFilter;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalesDetailClickhouseRepository {
    List<SalesDetailClickhouseInfoDto> findAllBySalesId(Collection<Long> id);

    List<SalesTopProductDto> topProducts(SalesFilter filter);

    SalesDetailDynamicsDto salesDetailDynamics(SalesFilter filter);
}
