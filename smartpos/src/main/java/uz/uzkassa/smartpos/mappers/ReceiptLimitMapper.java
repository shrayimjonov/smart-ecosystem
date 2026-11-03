package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptLimit;
import uz.uzkassa.smartpos.dto.ReceiptLimitDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReceiptLimitMapper {
    ReceiptLimit toEntity(ReceiptLimitDto receiptLimitDto);

    ReceiptLimitDto toDto(ReceiptLimit receiptLimit);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ReceiptLimit partialUpdate(ReceiptLimitDto receiptLimitDto, @MappingTarget ReceiptLimit receiptLimit);
}