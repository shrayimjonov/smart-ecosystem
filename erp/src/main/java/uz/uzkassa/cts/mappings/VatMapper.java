package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.dto.vat.VatItemDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VatMapper {

    VatItemDTO toItemDTO(Vat entity);
}

