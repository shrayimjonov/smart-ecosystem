package uz.uzkassa.smartpos.domain.postgres.receipt;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.dto.shift.ShiftReportListDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShiftReportMapper {
    @Mapping(source = "branch.name", target = "branchName")
    @Mapping(source = "terminal.serialNumber", target = "terminalSN")
    @Mapping(source = "number", target = "fiscalNumber")
    ShiftReportListDto toDto(ShiftReport shiftReport);
}