package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.report.ContractReportDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractReportMapper {

    ContractReportDTO toDTO(ContractReport entity);

    ContractReport toEntity(ContractReportDTO dto);

    ContractReport toDomainObject(ContractReportDTO dtoObject, @MappingTarget ContractReport contractReport);
}
