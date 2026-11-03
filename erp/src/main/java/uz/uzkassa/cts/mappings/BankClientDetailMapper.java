package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankClientDetailMapper {

    BankClientDetailsDto toDto(BankClientDetail entity);

    BankClientDto toDto(BankClientJournal entity);

    List<BankClientDetailsDto> toDto(List<BankClientDetail> entity);

    BankClientDetail toEntity(BankClientDetailsDto dto);
}
