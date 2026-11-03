package uz.uzkassa.smartpos.repository.clickhouse.report;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByCategoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByOneProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.report.*;
import uz.uzkassa.smartpos.filter.FashionReportFilter;

import java.util.List;

@Repository
public interface FashionReportRepository {
    Page<FashionReportByProductDto> reportByProduct(FashionReportFilter filter);

    List<FashionReportByColorGroupDto> reportByColorGroup(FashionReportFilter filter);

    List<FashionReportByOneProductDto> reportByOneProduct(FashionReportFilter filter);

    List<FashionReportByCategoryDto> reportByCategory(FashionReportFilter filter);

    Page<FashionReportByUserDto> reportByUser(FashionReportFilter filter);

    FashionReportByUserStatisticsDto reportByUserStatistics(FashionReportFilter filter);

    FashionReportByProductStatisticsDto reportByProductStatistics(FashionReportFilter filter);

    FashionReportByCategoryStatisticsDto reportByCategoryStatistics(FashionReportFilter filter);

    FashionReportByOneProductStatisticsDto reportByOneProductStatistics(FashionReportFilter filter);
}
