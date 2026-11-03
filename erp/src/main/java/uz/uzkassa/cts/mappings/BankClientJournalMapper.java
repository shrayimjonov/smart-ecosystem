package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankClientJournalMapper {

    BankClientDto toDto(BankClientJournal entity);

    BankClientJournal toEntity(BankClientDto dto);
}
