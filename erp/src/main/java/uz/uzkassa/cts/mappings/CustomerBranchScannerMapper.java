package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerBranchScanner;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.customerBranchScanner.CustomerBranchScannerDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerBranchScannerMapper {

    CommonCodeDTO toDTO(CustomerBranchScanner entity);

    CustomerBranchScanner toEntity(CustomerBranchScannerDTO dto);
}
