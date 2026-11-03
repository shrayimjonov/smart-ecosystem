package uz.uzkassa.smartpos.mappers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailCreateDto;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ReceiptMapperImpl.class)
class ReceiptMapperTest {
    @Autowired
    ReceiptMapper receiptMapper;

    @Test
    void update() {
        ReceiptDTO receiptDTO = Instancio.of(ReceiptDTO.class)
                .generate(Select.field(ReceiptDetailDTO::getVatBarcode), gen -> gen.string().length(17))
                .create();
        Receipt receipt = new Receipt();
        receiptMapper.update(receipt, receiptDTO);
        assertEquals(receipt.getBranchId(), receiptDTO.getBranchId());
        assertEquals(receipt.getCompanyId(), receiptDTO.getCompanyId());
        assertEquals(receipt.getCreatedById(), Optional.ofNullable(receipt.getCreatedById()).orElseGet(receiptDTO::getUserId));
        assertEquals(receipt.getCreatedDateTime(), Optional.ofNullable(receipt.getCreatedDateTime()).orElseGet(LocalDateTime::now));
        assertEquals(receipt.getUid(), Optional.ofNullable(receipt.getUid()).orElse(receiptDTO.getUid()));
        assertEquals(receipt.getLat(), receiptDTO.getLat());
        assertEquals(receipt.getLon(), receiptDTO.getLon());
        assertEquals(receipt.getOriginUid(), receiptDTO.getOriginUid());
        assertEquals(receipt.getFiscalSign(), receiptDTO.getFiscalSign());
        assertEquals(receipt.getFiscalUrl(), receiptDTO.getFiscalUrl());
        assertEquals(receipt.getShiftNumber(), receiptDTO.getShiftNo());
        assertEquals(receipt.getReceiptDateTime(), Optional.ofNullable(receiptDTO.getReceiptDateTime()).orElse(LocalDateTime.now()));
        assertEquals(receipt.getPayments(), receiptDTO.getPayments());
        assertEquals(receipt.getTotalCash(), receiptDTO.getTotalCash());
        assertEquals(receipt.getTotalCard(), receiptDTO.getTotalCard());
        assertEquals(receipt.getTotalCost(), receiptDTO.getTotalCost());
        assertEquals(receipt.getTotalPaid(), receiptDTO.getTotalPaid());
        assertEquals(receipt.getTotalDiscount(), receiptDTO.getTotalDiscount());
        assertEquals(receipt.getDiscountPercent(), receiptDTO.getDiscountPercent());
        assertEquals(receipt.getCustomerName(), receiptDTO.getCustomerName());
        assertEquals(receipt.getCustomerPhone(), receiptDTO.getCustomerPhone());
        assertEquals(receipt.isReadOnly(), receiptDTO.isReadOnly());
        assertEquals(receipt.isForceToPrint(), receiptDTO.isForceToPrint());
        assertEquals(receipt.getStatus(), Optional.ofNullable(receiptDTO.getStatus()).orElse(ReceiptStatus.DRAFT));
        assertEquals(receipt.getSource(), Optional.ofNullable(receipt.getSource()).orElse(receiptDTO.getSource()));
        assertEquals(receipt.getTotalNds(), receiptDTO.getTotalNds());
        assertEquals(receipt.getTotalExcise(), receiptDTO.getTotalExcise());
        assertEquals(receipt.getAdditional(), receiptDTO.getAdditional());
        assertEquals(receipt.getBillId(), receiptDTO.getBillId());
        assertEquals(receipt.isSendSms(), receiptDTO.isSendSms());
        assertEquals(receipt.getFacturaId(), Optional.ofNullable(receiptDTO.getFacturaId()).orElse(receipt.getFacturaId()));
        assertEquals(receipt.getReceiptDetails().size(), receiptDTO.getReceiptDetails().size());
        Map<Long, ReceiptDetailDTO> detailMap = receiptDTO.getReceiptDetails().stream().collect(Collectors.toMap(ReceiptDetailDTO::getProductId, Function.identity()));
        receipt.getReceiptDetails().forEach(detail -> {
            var detailDto = detailMap.get(detail.getProductId());
            assertEquals(detail.getProductId(), detailDto.getProductId());
            assertEquals(detail.getProductName(), detailDto.getProductName());
            assertEquals(detail.getProductBarcode(), detailDto.getProductBarcode());
            assertEquals(detail.getVatBarcode(), detailDto.getVatBarcode());
            assertEquals(detail.getCommittentTin(), detailDto.getCommittentTin());
            assertEquals(detail.getUnitId(), detailDto.getUnitId());
            assertEquals(detail.getUnitName(), detailDto.getUnitName());
            assertEquals(detail.getCategoryId(), detailDto.getCategoryId());
            assertEquals(detail.getCategoryName(), detailDto.getCategoryName());
            assertEquals(detail.getExcise(), detailDto.getExcise());
            assertEquals(detail.getExciseRate(), detailDto.getExciseRate());
            assertEquals(detail.getPrice(), detailDto.getPrice());
            assertEquals(detail.getQty(), detailDto.getQty());
            assertEquals(detail.getAmount(), detailDto.getAmount());
            assertEquals(detail.getDiscount(), detailDto.getDiscount());
            assertEquals(detail.getDiscountPercent(), detailDto.getDiscountPercent());
            assertEquals(detail.getNds(), detailDto.getNds());
            assertEquals(detail.getNdsPercent(), detailDto.getNdsPercent());
            assertEquals(detail.getMarks().size(), detailDto.getMarks().stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()).size());
            if (ReceiptStatus.DRAFT == receipt.getStatus() && CollectionUtils.isNotEmpty(detail.getMarks())) {
                assertEquals(detail.getMarks(), detailDto.getMarks().stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, null)).collect(Collectors.toSet()));
            }
            assertEquals(detail.getExtraDetail(), detailDto.getExtraDetail());
            assertEquals(detail.getReceipt(), receipt);
            assertEquals(detail.getReceiptDateTime(), receipt.getReceiptDateTime());
        });
    }

    @Test
    void toReceiptDto() {
        SalesCreateDto sales = Instancio.create(SalesCreateDto.class);
        ReceiptDTO receiptDTO = receiptMapper.toReceiptDto(sales);
        assertEquals(receiptDTO.getUid(), sales.getUid());
        assertEquals(receiptDTO.getReceiptDateTime(), sales.getCreatedDateTime());
        assertEquals(receiptDTO.getCreatedDateTime(), sales.getCreatedDateTime());
        assertEquals(receiptDTO.getOriginUid(), sales.getOriginUid());
        assertEquals(receiptDTO.getFiscalSign(), sales.getFiscalSign());
        assertEquals(receiptDTO.getFiscalUrl(), sales.getFiscalUrl());
        assertEquals(receiptDTO.getShiftNo(), sales.getShiftNumber());
        assertEquals(receiptDTO.getLat(), sales.getLat());
        assertEquals(receiptDTO.getLon(), sales.getLon());
        assertEquals(receiptDTO.getTotalCost(), sales.getTotalCost());
        assertEquals(receiptDTO.getTotalPaid(), sales.getTotalPaid());
        assertEquals(receiptDTO.getDiscountPercent(), sales.getDiscountPercent());
        assertEquals(receiptDTO.getStatus(), sales.getStatus() == SalesStatus.DRAFT ? ReceiptStatus.DRAFT : sales.getType().getReceiptStatus());
        assertEquals(receiptDTO.getPayments(), sales.getPayments());
        assertEquals(receiptDTO.getBranchId(), sales.getBranchId());
        assertEquals(receiptDTO.getCompanyId(), sales.getCompanyId());
        assertEquals(receiptDTO.getUserId(), sales.getCreatedById());
        assertEquals(receiptDTO.getTerminalSN(), sales.getTerminalSN());
        assertEquals(receiptDTO.getFacturaId(), sales.getFacturaId());
        assertEquals(receiptDTO.getCustomerName(), sales.getCustomerName());
        assertEquals(receiptDTO.getCustomerPhone(), sales.getCustomerPhone());
        assertEquals(receiptDTO.getCustomerId(), sales.getCustomerId());
        assertEquals(receiptDTO.getBillId(), sales.getBillId());
        assertEquals(receiptDTO.isSendSms(), sales.isSendSms());
        assertEquals(receiptDTO.getAdditional(), sales.getAdditional());

        assertEquals(receiptDTO.getReceiptDetails().size(), sales.getReceiptDetails().size());
        Map<Long, SalesDetailCreateDto> detailMap = sales.getReceiptDetails().stream().collect(Collectors.toMap(SalesDetailCreateDto::getProductId, Function.identity()));
        receiptDTO.getReceiptDetails().forEach(detailDto -> {
            var detail = detailMap.get(detailDto.getProductId());
            assertEquals(detailDto.getProductName(), detail.getProductName());
            assertEquals(detailDto.getProductBarcode(), detail.getProductBarcode());
            assertEquals(detailDto.getVatBarcode(), detail.getVatBarcode());
            assertEquals(detailDto.getUnitId(), detail.getUnitId());
            assertEquals(detailDto.getUnitName(), detail.getUnitName());
            assertEquals(detailDto.getExcise(), detail.getExcise());
            assertEquals(detailDto.getExciseRate(), detail.getExciseRate());
            assertEquals(detailDto.getPrice(), detail.getPrice());
            assertEquals(detailDto.getQty(), detail.getQty());
            assertEquals(detailDto.getAmount(), detail.getAmount());
            assertEquals(detailDto.getDiscountPercent(), detail.getDiscountPercent());
            assertEquals(detailDto.getDiscount(), detail.getDiscount());
            assertEquals(detailDto.getNdsPercent(), detail.getVatPercent());
            assertEquals(detailDto.getNds(), detail.getVat());
            assertEquals(detailDto.getMarks(), detail.getMarks());
            assertEquals(detailDto.getCommittentTin(), detail.getCommittentTin());
            assertEquals(detailDto.getCategoryId(), detail.getCategoryId());
            assertEquals(detailDto.getCategoryName(), detail.getCategoryName());
            assertEquals(detailDto.getExtraDetail(), detail.getExtraDetail());
        });

        assertEquals(receiptDTO.isForceToPrint(), sales.isForceToPrint());
        assertEquals(receiptDTO.getPromotionId(), sales.getPromotionId());
    }
}