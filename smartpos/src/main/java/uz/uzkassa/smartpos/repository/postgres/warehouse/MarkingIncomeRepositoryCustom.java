package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.warehouse.MarkingIncomeListDto;
import uz.uzkassa.smartpos.dto.warehouse.MarkingStatusReportDto;
import uz.uzkassa.smartpos.filter.MarkingIncomeFilter;

public interface MarkingIncomeRepositoryCustom {
    Page<MarkingIncomeListDto> findAllByFilter(MarkingIncomeFilter filter);

    Page<MarkingStatusReportDto> markingsReport(MarkingIncomeFilter filter);
}
