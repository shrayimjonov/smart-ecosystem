package uz.uzkassa.smartpos.mappers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.dto.receipt.TransitReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailCreateDto;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReceiptMapper {
    @Mapping(target = "totalLoyaltyCard", ignore = true)
    TransitReceiptDTO toTransitReceiptDto(Receipt entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "receiptDateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "facturaId", ignore = true)
    @Mapping(target = "shiftNumber", source = "shiftNo")
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "terminal", ignore = true)
    void update(@MappingTarget final Receipt receipt, final ReceiptDTO receiptDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "receiptDateTime", ignore = true)
    ReceiptDetail toDetail(ReceiptDetailDTO receiptDetailDTO);

    @AfterMapping
    default void afterMapping(@MappingTarget final Receipt receipt, final ReceiptDTO receiptDTO) {
        receipt.setCreatedById(Optional.ofNullable(receipt.getCreatedById()).orElseGet(receiptDTO::getUserId));
        receipt.setCreatedDateTime(Optional.ofNullable(receipt.getCreatedDateTime()).orElseGet(LocalDateTime::now));
        receipt.setUid(Optional.ofNullable(receipt.getUid()).orElse(receiptDTO.getUid()));
        receipt.setReceiptDateTime(Optional.ofNullable(receiptDTO.getReceiptDateTime()).orElseGet(LocalDateTime::now));
        receipt.setStatus(Optional.ofNullable(receiptDTO.getStatus()).orElse(ReceiptStatus.DRAFT));
        receipt.setSource(Optional.ofNullable(receipt.getSource()).orElse(receiptDTO.getSource()));
        receipt.setFacturaId(Optional.ofNullable(receiptDTO.getFacturaId()).orElse(receipt.getFacturaId()));
        receipt.getReceiptDetails().forEach(receiptDetail -> {
            receiptDetail.setReceipt(receipt);
            receiptDetail.setReceiptDateTime(receipt.getReceiptDateTime());
            if (CollectionUtils.isNotEmpty(receiptDetail.getMarks())) {
                receiptDetail.setMarks(receiptDetail.getMarks().stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
                if (ReceiptStatus.DRAFT == receipt.getStatus() && CollectionUtils.isNotEmpty(receiptDetail.getMarks())) {
                    var productGroup = MarkingUtils.getGroupCode(receiptDetail.getVatBarcode());
                    receiptDetail.setMarks(receiptDetail.getMarks().stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, productGroup)).collect(Collectors.toSet()));
                }
            }
        });
    }

    @Mapping(target = "receiptDateTime", source = "createdDateTime")
    @Mapping(target = "status", expression = "java(sales.getStatus() == uz.uzkassa.smartpos.enums.sales.SalesStatus.DRAFT ? uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT : sales.getType().getReceiptStatus())")
    @Mapping(target = "shiftNo", source = "shiftNumber")
    @Mapping(target = "userId", source = "createdById")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalCash", ignore = true)
    @Mapping(target = "totalCard", ignore = true)
    @Mapping(target = "totalLoyaltyCard", ignore = true)
    @Mapping(target = "totalDiscount", ignore = true)
    @Mapping(target = "totalExcise", ignore = true)
    @Mapping(target = "totalNds", ignore = true)
    @Mapping(target = "loyaltyCardId", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    @Mapping(target = "branchAddress", ignore = true)
    @Mapping(target = "terminalId", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "companyINN", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "readOnly", ignore = true)
    @Mapping(target = "positionCount", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "stockOutId", ignore = true)
    ReceiptDTO toReceiptDto(SalesCreateDto sales);

    @Mapping(target = "nds", source = "vat")
    @Mapping(target = "ndsPercent", source = "vatPercent")
    @Mapping(target = "salesPrice", ignore = true)
    @Mapping(target = "receiptId", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    ReceiptDetailDTO toReceiptDetailDto(SalesDetailCreateDto sales);
}
