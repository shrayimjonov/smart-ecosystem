package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.SaleAct;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaleActMapper {

    @Mapping(source = "creator.login", target = "creatorName")
    SaleActResponseDTO toDto(SaleAct saleAct);
}
