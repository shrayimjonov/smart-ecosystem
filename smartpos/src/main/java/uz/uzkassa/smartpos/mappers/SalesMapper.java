package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.dto.sales.*;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SalesMapper {
    @Mapping(target = "additional", source = "additional")
    @Mapping(target = "additional.customerName", source = "customerName")
    @Mapping(target = "additional.customerPhone", source = "customerPhone")
    @Mapping(target = "additional.billId", source = "billId")
    @Mapping(target = "additional.customerId", source = "customerId")
    @Mapping(target = "additional.newCustomer", source = "newCustomer")
    @Mapping(target = "additional.sendSms", source = "sendSms")
    @Mapping(target = "salesDetails", source = "receiptDetails")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "receiptDateTime", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "terminal", ignore = true)
    Sales toEntity(SalesCreateDto salesCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    SalesDetail toSalesDetail(SalesDetailCreateDto salesDetailDto);

    @Mapping(target = "createdDateTime", ignore = true)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(SalesCreateDto salesCreateDto, @MappingTarget Sales sales);

    @AfterMapping
    default void linkSalesDetails(@MappingTarget Sales sales) {
        sales.getSalesDetails().forEach(salesDetail -> salesDetail.setSales(sales));
    }

    @Mapping(target = "receiptDetails", source = "salesDetails")
    @Mapping(target = "billId", source = "additional.billId")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyINN", source = "company.inn")
    @Mapping(target = "user", source = "createdBy")
    @Mapping(target = "customerName", source = "additional.customerName")
    @Mapping(target = "customerPhone", source = "additional.customerPhone")
    @Mapping(target = "shiftNo", source = "shiftNumber")
    SalesDto toDto(Sales sales);

    @Mapping(target = "nds", source = "vat")
    @Mapping(target = "ndsPercent", source = "vatPercent")
    @Mapping(target = "salesId", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    SalesDetailDto toDetailDto(SalesDetail salesDetail);

    @Mapping(target = "uid", expression = "java(withoutFiscalData? null: sales.getUid())")
    @Mapping(target = "fiscalSign", expression = "java(withoutFiscalData? null: sales.getFiscalSign())")
    @Mapping(target = "fiscalUrl", expression = "java(withoutFiscalData? null: sales.getFiscalUrl())")
    @Mapping(target = "receiptDateTime", expression = "java(java.util.Optional.ofNullable(sales.getReceiptDateTime()).orElse(sales.getCreatedDateTime()))")
    @Mapping(target = "customerName", source = "sales.additional.customerName")
    @Mapping(target = "customerPhone", source = "sales.additional.customerPhone")
    @Mapping(target = "billId", source = "sales.additional.billId")
    @Mapping(target = "sendSms", source = "sales.additional.sendSms")
    @Mapping(target = "additional", source = "sales.additional.additional")
    @Mapping(target = "branchName", source = "sales.branch.name")
    @Mapping(target = "branchAddress", source = "sales.branch.address")
    @Mapping(target = "companyName", source = "sales.company.name")
    @Mapping(target = "companyINN", source = "sales.company.inn")
    @Mapping(target = "status", expression = "java(sales.getStatus() == uz.uzkassa.smartpos.enums.sales.SalesStatus.DRAFT ? uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT : sales.getType().getReceiptStatus())")
    @Mapping(target = "shiftNo", source = "sales.shiftNumber")
    @Mapping(target = "userId", source = "sales.createdById")
    @Mapping(target = "user", source = "sales.createdBy.shortDTO")
    @Mapping(target = "receiptDetails", source = "sales.salesDetails")
    @Mapping(target = "totalCash", ignore = true)
    @Mapping(target = "totalCard", ignore = true)
    @Mapping(target = "totalLoyaltyCard", ignore = true)
    @Mapping(target = "totalDiscount", ignore = true)
    @Mapping(target = "totalExcise", ignore = true)
    @Mapping(target = "totalNds", ignore = true)
    @Mapping(target = "loyaltyCardId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "terminalId", ignore = true)
    @Mapping(target = "readOnly", ignore = true)
    @Mapping(target = "forceToPrint", ignore = true)
    @Mapping(target = "positionCount", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "stockOutId", ignore = true)
    ReceiptDTO toDto(Sales sales, @Context boolean withMarks, boolean withoutFiscalData);

    @Mapping(target = "marks", expression = "java(withMarks? salesDetail.getMarks(): null)")
    @Mapping(target = "nds", source = "salesDetail.vat")
    @Mapping(target = "ndsPercent", source = "salesDetail.vatPercent")
    @Mapping(target = "salesPrice", ignore = true)
    @Mapping(target = "receiptId", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    ReceiptDetailDTO toDto(SalesDetail salesDetail, @Context boolean withMarks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    @Mapping(target = "receiptDateTime", source = "fiscalCreatedDate")
    @Mapping(target = "additional.samModuleSerialNumber", source = "samModuleSerialNumber")
    @Mapping(target = "additional.receiptSeq", source = "receiptSeq")
    @Mapping(target = "additional.fiscalCreatedDate", source = "fiscalCreatedDate")
    @Mapping(target = "additional.providerId", source = "providerId")
    @Mapping(target = "additional.transactionId", source = "transactionId")
    @Mapping(target = "additional.numberInShift", source = "numberInShift")
    @Mapping(target = "additional.epaymentId", source = "epaymentId")
    @Mapping(target = "additional.voucherId", source = "voucherId")
    @Mapping(target = "additional.carPlateNumber", source = "carPlateNumber")
    @Mapping(target = "additional.columnNumber", source = "columnNumber")
    @Mapping(target = "additional.cardType", source = "cardType")
    @Mapping(target = "additional.rrn", source = "rrn")
    @Mapping(target = "additional.terminalVersion", source = "terminalVersion")
    @Mapping(target = "additional.homeServiceAccount", source = "homeServiceAccount")
    @Mapping(target = "additional.cardPan", source = "cardPan")
    @Mapping(target = "additional.appType", source = "appType")
    void updateFiscalData(@MappingTarget Sales sales, SalesFiscalDto fiscalDto);

    @Mapping(target = "terminalId", source = "terminal.id")
    @Mapping(target = "customerName", source = "additional.customerName")
    @Mapping(target = "customerPhone", source = "additional.customerPhone")
    @Mapping(target = "additional", source = "additional.additional")
    @Mapping(target = "billId", source = "additional.billId")
    @Mapping(target = "sendSms", source = "additional.sendSms")
    @Mapping(target = "status", source = "type.receiptStatus")
    @Mapping(target = "createdDateTime", source = "createdAt")
    @Mapping(target = "receiptDetails", source = "salesDetails")
    @Mapping(target = "totalCash", ignore = true)
    @Mapping(target = "totalCard", ignore = true)
    @Mapping(target = "totalDiscount", ignore = true)
    @Mapping(target = "totalExcise", ignore = true)
    @Mapping(target = "totalNds", ignore = true)
    @Mapping(target = "readOnly", ignore = true)
    @Mapping(target = "forceToPrint", ignore = true)
    Receipt toReceiptFromSales(Sales sales);

    @Mapping(target = "nds", source = "vat")
    @Mapping(target = "ndsPercent", source = "vatPercent")
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "receiptDateTime", ignore = true)
    ReceiptDetail toReceiptDetail(SalesDetail salesDetail);

    @AfterMapping
    default void linkReceiptDetails(@MappingTarget Receipt receipt) {
        receipt.getReceiptDetails().forEach(receiptDetail -> receiptDetail.setReceipt(receipt));
    }

    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "terminalSN", source = "terminal.serialNumber")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyINN", source = "company.inn")
    @Mapping(target = "shiftNo", source = "shiftNumber")
    @Mapping(target = "user.id", source = "createdBy.id")
    @Mapping(target = "user.name", source = "createdBy.fullNameStr")
    @Mapping(target = "type", source = "status.salesType")
    @Mapping(target = "status", expression = "java(receipt.getStatus() == uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT ? uz.uzkassa.smartpos.enums.sales.SalesStatus.DRAFT : uz.uzkassa.smartpos.enums.sales.SalesStatus.COMPLETED)")
    @Mapping(target = "payments", expression = "java(getPayments(receipt))")
    @Mapping(target = "promotionId", ignore = true)
    SalesDto toSalesDtoFromReceipt(Receipt receipt);

    default Map<PaymentType, BigDecimal> getPayments(Receipt receipt) {
        Map<PaymentType, BigDecimal> payments = new HashMap<>();
        if (receipt.getPayments() == null) {
            payments.put(PaymentType.CASH, receipt.getTotalCash());
            payments.put(PaymentType.CARD, receipt.getTotalCard());
            payments.put(PaymentType.NDS, receipt.getTotalNds());
            payments.put(PaymentType.EXCISE, receipt.getTotalExcise());
            payments.put(PaymentType.DISCOUNT, receipt.getTotalDiscount());
            return payments;
        }
        EnumSet<PaymentType> receiptTotalTypes = EnumSet.of(PaymentType.CASH, PaymentType.CARD, PaymentType.NDS, PaymentType.EXCISE, PaymentType.DISCOUNT);
        receiptTotalTypes.forEach(type -> {
            if (receipt.getPayments().containsKey(type) && !BigDecimal.ZERO.equals(receipt.getPayments().get(type))) {
                payments.put(type, receipt.getPayments().get(type));
            }
        });
        if (receipt.getTotalCash() != null && receipt.getTotalCash().compareTo(BigDecimal.ZERO) > 0) {
            payments.put(PaymentType.CASH, receipt.getTotalCash());
        }
        if (receipt.getTotalCard() != null && receipt.getTotalCard().compareTo(BigDecimal.ZERO) > 0) {
            payments.put(PaymentType.CARD, receipt.getTotalCard());
        }
        if (receipt.getTotalNds() != null && receipt.getTotalNds().compareTo(BigDecimal.ZERO) > 0) {
            payments.put(PaymentType.NDS, receipt.getTotalNds());
        }
        if (receipt.getTotalExcise() != null && receipt.getTotalExcise().compareTo(BigDecimal.ZERO) > 0) {
            payments.put(PaymentType.EXCISE, receipt.getTotalExcise());
        }
        if (receipt.getTotalDiscount() != null && receipt.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0) {
            payments.put(PaymentType.DISCOUNT, receipt.getTotalDiscount());
        }
        return payments;
    }

    @BeanMapping(ignoreByDefault = true, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "receiptDateTime", source = "receiptDateTime")
    @Mapping(target = "status", expression = "java(uz.uzkassa.smartpos.enums.sales.SalesStatus.COMPLETED)")
    @Mapping(target = "additional.samModuleSerialNumber", source = "additional.samModuleSerialNumber")
    @Mapping(target = "additional.receiptSeq", source = "additional.receiptSeq")
    @Mapping(target = "additional.fiscalCreatedDate", source = "additional.fiscalCreatedDate")
    @Mapping(target = "additional.providerId", source = "additional.providerId")
    @Mapping(target = "additional.transactionId", source = "additional.transactionId")
    @Mapping(target = "additional.numberInShift", source = "additional.numberInShift")
    @Mapping(target = "additional.epaymentId", source = "additional.epaymentId")
    @Mapping(target = "additional.voucherId", source = "additional.voucherId")
    @Mapping(target = "additional.carPlateNumber", source = "additional.carPlateNumber")
    @Mapping(target = "additional.columnNumber", source = "additional.columnNumber")
    @Mapping(target = "additional.cardType", source = "additional.cardType")
    @Mapping(target = "additional.rrn", source = "additional.rrn")
    @Mapping(target = "additional.terminalVersion", source = "additional.terminalVersion")
    @Mapping(target = "additional.homeServiceAccount", source = "additional.homeServiceAccount")
    @Mapping(target = "additional.cardPan", source = "additional.cardPan")
    @Mapping(target = "additional.appType", source = "additional.appType")
    @Mapping(target = "additional.customerName", ignore = true)
    @Mapping(target = "additional.customerPhone", ignore = true)
    @Mapping(target = "additional.billId", ignore = true)
    @Mapping(target = "additional.sendSms", ignore = true)
    @Mapping(target = "additional.customerId", ignore = true)
    @Mapping(target = "additional.newCustomer", ignore = true)
    Sales updateCompletedSaleFromReceipt(@MappingTarget Sales sale, Receipt receipt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", source = "status.salesType")
    @Mapping(target = "status", expression = "java(receipt.getStatus() == uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT ? uz.uzkassa.smartpos.enums.sales.SalesStatus.DRAFT : uz.uzkassa.smartpos.enums.sales.SalesStatus.COMPLETED)")
    @Mapping(target = "source", expression = "java(java.util.Optional.ofNullable(receipt.getSource()).orElse(uz.uzkassa.smartpos.enums.Source.TERMINAL))")
    @Mapping(target = "payments", expression = "java(getPayments(receipt))")
    @Mapping(target = "terminalSN", source = "terminal.serialNumber")
    @Mapping(target = "additional", source = "additional")
    @Mapping(target = "additional.customerName", source = "customerName")
    @Mapping(target = "additional.customerPhone", source = "customerPhone")
    @Mapping(target = "additional.billId", source = "billId")
    @Mapping(target = "additional.sendSms", source = "sendSms")
    @Mapping(target = "salesDetails", source = "receiptDetails")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "promotionId", ignore = true)
    Sales toSalesFromReceipt(Receipt receipt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vat", source = "nds")
    @Mapping(target = "vatPercent", source = "ndsPercent")
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "promotionId", ignore = true)
    SalesDetail toDetail(ReceiptDetail detail);
}