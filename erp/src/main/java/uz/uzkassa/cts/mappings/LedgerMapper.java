package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.Ledger;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.dto.ledger.request.LedgerDto;
import uz.uzkassa.cts.dto.ledger.response.LedgerResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LedgerMapper {

    LedgerDto toDto(Ledger entity);

    Ledger toEntity(LedgerDto dto);

    Ledger toEntity(LedgerDto dto, @MappingTarget Ledger entity);

    @Mapping(source = "ledger.description", target = "description")
    @Mapping(source = "ledger.referenceTo", target = "referenceTo")
    LedgerResponseDto toDto(LedgerDetail ledgerDetail);
}
