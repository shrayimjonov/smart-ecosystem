package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.dto.bankclientdetailrejected.response.BankClientDetailRejectedResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankClientDetailRejectedMapper {

    BankClientDetailRejectedResponseDto toDto(BankClientDetailRejected entity);
}
