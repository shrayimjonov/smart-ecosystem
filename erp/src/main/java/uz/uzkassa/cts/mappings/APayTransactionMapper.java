package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.APayTransaction;
import uz.uzkassa.cts.dto.apaytransaction.request.APayTransactionRequestDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayTransactionResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface APayTransactionMapper {

    APayTransactionResponseDto toDto(APayTransaction entity);

    APayTransaction toEntity(APayTransactionRequestDto dto);
}
