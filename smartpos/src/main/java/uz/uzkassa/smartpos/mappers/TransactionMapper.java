package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionCreateDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class})
public interface TransactionMapper {
    @Mapping(target = "deviceSN", source = "terminalSN")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "source", ignore = true)
    Transaction toEntity(TransactionCreateDto dto);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(@MappingTarget Transaction entity, TransactionCreateDto dto);

    TransactionDto toDto(Transaction transaction);
}
