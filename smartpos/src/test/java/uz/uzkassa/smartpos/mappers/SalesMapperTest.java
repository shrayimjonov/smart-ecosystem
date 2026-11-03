package uz.uzkassa.smartpos.mappers;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailCreateDto;
import uz.uzkassa.smartpos.dto.sales.SalesDto;
import uz.uzkassa.smartpos.dto.sales.SalesFiscalDto;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = SalesMapperImpl.class)
class SalesMapperTest {
    @Autowired
    SalesMapper salesMapper;

    @Test
    void toEntity() {
        SalesCreateDto salesCreateDto = Instancio.create(SalesCreateDto.class);
        Sales result = salesMapper.toEntity(salesCreateDto);
        assertEquals(result.getUid(), salesCreateDto.getUid());
        assertEquals(result.getCreatedDateTime(), salesCreateDto.getCreatedDateTime());
        assertEquals(result.getFiscalSign(), salesCreateDto.getFiscalSign());
        assertEquals(result.getFiscalUrl(), salesCreateDto.getFiscalUrl());
        assertEquals(result.getShiftNumber(), salesCreateDto.getShiftNumber());
        assertEquals(result.getLat(), salesCreateDto.getLat());
        assertEquals(result.getLon(), salesCreateDto.getLon());
        assertEquals(result.getTotalCost(), salesCreateDto.getTotalCost());
        assertEquals(result.getTotalPaid(), salesCreateDto.getTotalPaid());
        assertEquals(result.getDiscountPercent(), salesCreateDto.getDiscountPercent());
        salesCreateDto.getPayments().forEach(((paymentType, bigDecimal) -> assertEquals(result.getPayments().get(paymentType), bigDecimal)));
        assertEquals(result.getBranchId(), salesCreateDto.getBranchId());
        assertEquals(result.getCompanyId(), salesCreateDto.getCompanyId());
        assertEquals(result.getCreatedById(), salesCreateDto.getCreatedById());
        assertEquals(result.getTerminalSN(), salesCreateDto.getTerminalSN());
        assertEquals(result.getFacturaId(), salesCreateDto.getFacturaId());
        assertEquals(result.getAdditional().getCustomerName(), salesCreateDto.getCustomerName());
        assertEquals(result.getAdditional().getCustomerPhone(), salesCreateDto.getCustomerPhone());
        assertEquals(result.getAdditional().getBillId(), salesCreateDto.getBillId());
        assertEquals(result.getAdditional().isSendSms(), salesCreateDto.isSendSms());

        assertEquals(result.getAdditional().getSamModuleSerialNumber(), salesCreateDto.getAdditional().getSamModuleSerialNumber());
        assertEquals(result.getAdditional().getReceiptSeq(), salesCreateDto.getAdditional().getReceiptSeq());
        assertEquals(result.getAdditional().getFiscalCreatedDate(), salesCreateDto.getAdditional().getFiscalCreatedDate());
        assertEquals(result.getAdditional().getProviderId(), salesCreateDto.getAdditional().getProviderId());
        assertEquals(result.getAdditional().getTransactionId(), salesCreateDto.getAdditional().getTransactionId());
        assertEquals(result.getAdditional().getNumberInShift(), salesCreateDto.getAdditional().getNumberInShift());
        assertEquals(result.getAdditional().getEpaymentId(), salesCreateDto.getAdditional().getEpaymentId());
        assertEquals(result.getAdditional().getVoucherId(), salesCreateDto.getAdditional().getVoucherId());
        assertEquals(result.getAdditional().getCarPlateNumber(), salesCreateDto.getAdditional().getCarPlateNumber());
        assertEquals(result.getAdditional().getColumnNumber(), salesCreateDto.getAdditional().getColumnNumber());
        assertEquals(result.getAdditional().getCardType(), salesCreateDto.getAdditional().getCardType());
        assertEquals(result.getAdditional().getRrn(), salesCreateDto.getAdditional().getRrn());
        assertEquals(result.getAdditional().getTerminalVersion(), salesCreateDto.getAdditional().getTerminalVersion());
        assertEquals(result.getAdditional().isForCredit(), salesCreateDto.getAdditional().isForCredit());
        assertEquals(result.getAdditional().getMonth(), salesCreateDto.getAdditional().getMonth());
        assertEquals(result.getAdditional().getHomeServiceAccount(), salesCreateDto.getAdditional().getHomeServiceAccount());
        assertEquals(result.getAdditional().getCardPan(), salesCreateDto.getAdditional().getCardPan());
        assertEquals(result.getAdditional().getAppType(), salesCreateDto.getAdditional().getAppType());

        assertEquals(result.getSalesDetails().size(), salesCreateDto.getReceiptDetails().size());
        Map<Long, SalesDetailCreateDto> detailMap = salesCreateDto.getReceiptDetails().stream().collect(Collectors.toMap(SalesDetailCreateDto::getProductId, Function.identity()));
        result.getSalesDetails().forEach(salesDetail -> {
            assertEquals(result, salesDetail.getSales());
            var detail = detailMap.get(salesDetail.getProductId());
            assertEquals(salesDetail.getProductName(), detail.getProductName());
            assertEquals(salesDetail.getProductBarcode(), detail.getProductBarcode());
            assertEquals(salesDetail.getVatBarcode(), detail.getVatBarcode());
            assertEquals(salesDetail.getCommittentTin(), detail.getCommittentTin());
            assertEquals(salesDetail.getUnitId(), detail.getUnitId());
            assertEquals(salesDetail.getUnitName(), detail.getUnitName());
            assertEquals(salesDetail.getCategoryId(), detail.getCategoryId());
            assertEquals(salesDetail.getCategoryName(), detail.getCategoryName());
            assertEquals(salesDetail.getPrice(), detail.getPrice());
            assertEquals(salesDetail.getQty(), detail.getQty());
            assertEquals(salesDetail.getAmount(), detail.getAmount());
            assertEquals(salesDetail.getDiscount(), detail.getDiscount());
            assertEquals(salesDetail.getDiscountPercent(), detail.getDiscountPercent());
            assertEquals(salesDetail.getVat(), detail.getVat());
            assertEquals(salesDetail.getVatPercent(), detail.getVatPercent());
            assertEquals(salesDetail.getExciseRate(), detail.getExciseRate());
            assertEquals(salesDetail.getExcise(), detail.getExcise());
            assertEquals(salesDetail.getMarks(), detail.getMarks());
            assertEquals(salesDetail.getExtraDetail(), detail.getExtraDetail());
            assertEquals(salesDetail.getPromotionId(), detail.getPromotionId());
        });

        assertEquals(result.getPromotionId(), salesCreateDto.getPromotionId());
    }

    @Test
    void partialUpdate() {
        SalesCreateDto salesCreateDto = Instancio.create(SalesCreateDto.class);
        Sales result = Instancio.create(Sales.class);
        salesMapper.partialUpdate(salesCreateDto, result);
        assertEquals(result.getUid(), salesCreateDto.getUid());
        assertNotEquals(result.getCreatedDateTime(), salesCreateDto.getCreatedDateTime());
        assertEquals(result.getFiscalSign(), salesCreateDto.getFiscalSign());
        assertEquals(result.getFiscalUrl(), salesCreateDto.getFiscalUrl());
        assertEquals(result.getShiftNumber(), salesCreateDto.getShiftNumber());
        assertEquals(result.getLat(), salesCreateDto.getLat());
        assertEquals(result.getLon(), salesCreateDto.getLon());
        assertEquals(result.getTotalCost(), salesCreateDto.getTotalCost());
        assertEquals(result.getTotalPaid(), salesCreateDto.getTotalPaid());
        assertEquals(result.getDiscountPercent(), salesCreateDto.getDiscountPercent());
        salesCreateDto.getPayments().forEach(((paymentType, bigDecimal) -> assertEquals(result.getPayments().get(paymentType), bigDecimal)));
        assertEquals(result.getBranchId(), salesCreateDto.getBranchId());
        assertEquals(result.getCompanyId(), salesCreateDto.getCompanyId());
        assertEquals(result.getCreatedById(), salesCreateDto.getCreatedById());
        assertEquals(result.getTerminalSN(), salesCreateDto.getTerminalSN());
        assertEquals(result.getFacturaId(), salesCreateDto.getFacturaId());
        assertEquals(result.getAdditional().getCustomerName(), salesCreateDto.getCustomerName());
        assertEquals(result.getAdditional().getCustomerPhone(), salesCreateDto.getCustomerPhone());
        assertEquals(result.getAdditional().getBillId(), salesCreateDto.getBillId());
        assertEquals(result.getAdditional().isSendSms(), salesCreateDto.isSendSms());

        assertEquals(result.getAdditional().getSamModuleSerialNumber(), salesCreateDto.getAdditional().getSamModuleSerialNumber());
        assertEquals(result.getAdditional().getReceiptSeq(), salesCreateDto.getAdditional().getReceiptSeq());
        assertEquals(result.getAdditional().getFiscalCreatedDate(), salesCreateDto.getAdditional().getFiscalCreatedDate());
        assertEquals(result.getAdditional().getProviderId(), salesCreateDto.getAdditional().getProviderId());
        assertEquals(result.getAdditional().getTransactionId(), salesCreateDto.getAdditional().getTransactionId());
        assertEquals(result.getAdditional().getNumberInShift(), salesCreateDto.getAdditional().getNumberInShift());
        assertEquals(result.getAdditional().getEpaymentId(), salesCreateDto.getAdditional().getEpaymentId());
        assertEquals(result.getAdditional().getVoucherId(), salesCreateDto.getAdditional().getVoucherId());
        assertEquals(result.getAdditional().getCarPlateNumber(), salesCreateDto.getAdditional().getCarPlateNumber());
        assertEquals(result.getAdditional().getColumnNumber(), salesCreateDto.getAdditional().getColumnNumber());
        assertEquals(result.getAdditional().getCardType(), salesCreateDto.getAdditional().getCardType());
        assertEquals(result.getAdditional().getRrn(), salesCreateDto.getAdditional().getRrn());
        assertEquals(result.getAdditional().getTerminalVersion(), salesCreateDto.getAdditional().getTerminalVersion());
        assertEquals(result.getAdditional().isForCredit(), salesCreateDto.getAdditional().isForCredit());
        assertEquals(result.getAdditional().getMonth(), salesCreateDto.getAdditional().getMonth());
        assertEquals(result.getAdditional().getHomeServiceAccount(), salesCreateDto.getAdditional().getHomeServiceAccount());
        assertEquals(result.getAdditional().getCardPan(), salesCreateDto.getAdditional().getCardPan());
        assertEquals(result.getAdditional().getAppType(), salesCreateDto.getAdditional().getAppType());

        assertEquals(result.getSalesDetails().size(), salesCreateDto.getReceiptDetails().size());
        Map<Long, SalesDetailCreateDto> detailMap = salesCreateDto.getReceiptDetails().stream().collect(Collectors.toMap(SalesDetailCreateDto::getProductId, Function.identity()));
        result.getSalesDetails().forEach(salesDetail -> {
            assertEquals(result, salesDetail.getSales());
            var detail = detailMap.get(salesDetail.getProductId());
            assertEquals(salesDetail.getProductName(), detail.getProductName());
            assertEquals(salesDetail.getProductBarcode(), detail.getProductBarcode());
            assertEquals(salesDetail.getVatBarcode(), detail.getVatBarcode());
            assertEquals(salesDetail.getCommittentTin(), detail.getCommittentTin());
            assertEquals(salesDetail.getUnitId(), detail.getUnitId());
            assertEquals(salesDetail.getUnitName(), detail.getUnitName());
            assertEquals(salesDetail.getCategoryId(), detail.getCategoryId());
            assertEquals(salesDetail.getCategoryName(), detail.getCategoryName());
            assertEquals(salesDetail.getPrice(), detail.getPrice());
            assertEquals(salesDetail.getQty(), detail.getQty());
            assertEquals(salesDetail.getAmount(), detail.getAmount());
            assertEquals(salesDetail.getDiscount(), detail.getDiscount());
            assertEquals(salesDetail.getDiscountPercent(), detail.getDiscountPercent());
            assertEquals(salesDetail.getVat(), detail.getVat());
            assertEquals(salesDetail.getVatPercent(), detail.getVatPercent());
            assertEquals(salesDetail.getExciseRate(), detail.getExciseRate());
            assertEquals(salesDetail.getExcise(), detail.getExcise());
            assertEquals(salesDetail.getMarks(), detail.getMarks());
            assertEquals(salesDetail.getExtraDetail(), detail.getExtraDetail());
            assertEquals(salesDetail.getPromotionId(), detail.getPromotionId());
        });

        assertEquals(result.getPromotionId(), salesCreateDto.getPromotionId());
    }

    @Test
    void toDto() {
        Sales sale = Instancio.create(Sales.class);
        SalesDto dto = salesMapper.toDto(sale);
        assertEquals(dto.getId(), sale.getId());
        assertEquals(dto.getUid(), sale.getUid());
        assertEquals(dto.getOriginUid(), sale.getOriginUid());
        assertEquals(dto.getFiscalSign(), sale.getFiscalSign());
        assertEquals(dto.getFiscalUrl(), sale.getFiscalUrl());
        assertEquals(dto.getCreatedDateTime(), sale.getCreatedDateTime());
        assertEquals(dto.getPayments(), sale.getPayments());
        assertEquals(dto.getTotalCost(), sale.getTotalCost());
        assertEquals(dto.getDiscountPercent(), sale.getDiscountPercent());
        assertEquals(dto.getTotalPaid(), sale.getTotalPaid());
        assertEquals(dto.getBillId(), sale.getAdditional().getBillId());
        assertEquals(dto.getBranchId(), sale.getBranchId());
        assertEquals(dto.getBranchName(), sale.getBranch().getName());
        assertEquals(dto.getLat(), sale.getLat());
        assertEquals(dto.getLon(), sale.getLon());
        assertEquals(dto.getTerminalSN(), sale.getTerminalSN());
        assertEquals(dto.getCompanyId(), sale.getCompanyId());
        assertEquals(dto.getCompanyName(), sale.getCompany().getName());
        assertEquals(dto.getCompanyINN(), sale.getCompany().getInn());
        assertEquals(dto.getShiftNo(), sale.getShiftNumber());

        assertEquals(sale.getSalesDetails().size(), dto.getReceiptDetails().size());
        Map<Long, SalesDetail> detailMap = sale.getSalesDetails().stream().collect(Collectors.toMap(SalesDetail::getProductId, Function.identity()));
        dto.getReceiptDetails().forEach(detailDto -> {
            var detail = detailMap.get(detailDto.getProductId());
            assertEquals(detailDto.getProductName(), detail.getProductName());
            assertEquals(detailDto.getProductBarcode(), detail.getProductBarcode());
            assertEquals(detailDto.getVatBarcode(), detail.getVatBarcode());
            assertEquals(detailDto.getCommittentTin(), detail.getCommittentTin());
            assertEquals(detailDto.getUnitId(), detail.getUnitId());
            assertEquals(detailDto.getUnitName(), detail.getUnitName());
            assertEquals(detailDto.getCategoryId(), detail.getCategoryId());
            assertEquals(detailDto.getCategoryName(), detail.getCategoryName());
            assertEquals(detailDto.getPrice(), detail.getPrice());
            assertEquals(detailDto.getQty(), detail.getQty());
            assertEquals(detailDto.getAmount(), detail.getAmount());
            assertEquals(detailDto.getDiscount(), detail.getDiscount());
            assertEquals(detailDto.getDiscountPercent(), detail.getDiscountPercent());
            assertEquals(detailDto.getNds(), detail.getVat());
            assertEquals(detailDto.getNdsPercent(), detail.getVatPercent());
            assertEquals(detailDto.getExciseRate(), detail.getExciseRate());
            assertEquals(detailDto.getExcise(), detail.getExcise());
            assertEquals(detailDto.getMarks(), detail.getMarks());
            assertEquals(detailDto.getExtraDetail(), detail.getExtraDetail());
        });

        assertEquals(dto.getUser().getId(), sale.getCreatedBy().getId());
        assertEquals(dto.getUser().getName(), sale.getCreatedBy().getName());

        assertEquals(dto.getType(), sale.getType());
        assertEquals(dto.getStatus(), sale.getStatus());
        assertEquals(dto.getSource(), sale.getSource());
        assertEquals(dto.getPromotionId(), sale.getPromotionId());

        assertEquals(sale.getAdditional().getSamModuleSerialNumber(), dto.getAdditional().getSamModuleSerialNumber());
        assertEquals(sale.getAdditional().getReceiptSeq(), dto.getAdditional().getReceiptSeq());
        assertEquals(sale.getAdditional().getFiscalCreatedDate(), dto.getAdditional().getFiscalCreatedDate());
        assertEquals(sale.getAdditional().getProviderId(), dto.getAdditional().getProviderId());
        assertEquals(sale.getAdditional().getTransactionId(), dto.getAdditional().getTransactionId());
        assertEquals(sale.getAdditional().getNumberInShift(), dto.getAdditional().getNumberInShift());
        assertEquals(sale.getAdditional().getEpaymentId(), dto.getAdditional().getEpaymentId());
        assertEquals(sale.getAdditional().getVoucherId(), dto.getAdditional().getVoucherId());
        assertEquals(sale.getAdditional().getCarPlateNumber(), dto.getAdditional().getCarPlateNumber());
        assertEquals(sale.getAdditional().getColumnNumber(), dto.getAdditional().getColumnNumber());
        assertEquals(sale.getAdditional().getCardType(), dto.getAdditional().getCardType());
        assertEquals(sale.getAdditional().getRrn(), dto.getAdditional().getRrn());
        assertEquals(sale.getAdditional().getTerminalVersion(), dto.getAdditional().getTerminalVersion());
        assertEquals(sale.getAdditional().isForCredit(), dto.getAdditional().isForCredit());
        assertEquals(sale.getAdditional().getMonth(), dto.getAdditional().getMonth());
        assertEquals(sale.getAdditional().getHomeServiceAccount(), dto.getAdditional().getHomeServiceAccount());
        assertEquals(sale.getAdditional().getCardPan(), dto.getAdditional().getCardPan());
        assertEquals(sale.getAdditional().getAppType(), dto.getAdditional().getAppType());

        assertEquals(dto.getCustomerName(), sale.getAdditional().getCustomerName());
        assertEquals(dto.getCustomerPhone(), sale.getAdditional().getCustomerPhone());
        assertEquals(dto.getReceiptDateTime(), sale.getReceiptDateTime());
        assertEquals(dto.getFacturaId(), sale.getFacturaId());
    }

    @Test
    void toReceiptDto() {
        Sales sales = Instancio.create(Sales.class);
        sales.getSalesDetails().forEach(salesDetail -> salesDetail.setSales(sales));
        ReceiptDTO dto = salesMapper.toDto(sales, true, false);
        assertEquals(dto.getId(), sales.getId());
        assertEquals(dto.getOriginUid(), sales.getOriginUid());
        assertEquals(dto.getFiscalSign(), sales.getFiscalSign());
        assertEquals(dto.getFiscalUrl(), sales.getFiscalUrl());
        assertEquals(dto.getCreatedDateTime(), sales.getCreatedDateTime());
        assertEquals(dto.getPayments(), sales.getPayments());
        assertEquals(dto.getTotalCost(), sales.getTotalCost());
        assertEquals(dto.getDiscountPercent(), sales.getDiscountPercent());
        assertEquals(dto.getTotalPaid(), sales.getTotalPaid());
        assertEquals(dto.getBillId(), sales.getAdditional().getBillId());
        assertEquals(dto.getBranchId(), sales.getBranchId());
        assertEquals(dto.getBranchName(), sales.getBranch().getName());
        assertEquals(dto.getBranchAddress(), sales.getBranch().getAddress());
        assertEquals(dto.getTerminalSN(), sales.getTerminalSN());
        assertEquals(dto.getCompanyId(), sales.getCompanyId());
        assertEquals(dto.getCompanyName(), sales.getCompany().getName());
        assertEquals(dto.getCompanyINN(), sales.getCompany().getInn());
        assertEquals(dto.getShiftNo(), sales.getShiftNumber());

        assertEquals(dto.getReceiptDetails().size(), sales.getSalesDetails().size());
        Map<Long, SalesDetail> detailMap = sales.getSalesDetails().stream().collect(Collectors.toMap(SalesDetail::getProductId, Function.identity()));
        dto.getReceiptDetails().forEach(detailDto -> {
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

        assertEquals(dto.getUser().getId(), sales.getCreatedBy().getId());
        assertEquals(dto.getUser().getFullName().getFirstName(), sales.getCreatedBy().getFullName().getFirstName());
        assertEquals(dto.getUser().getFullName().getLastName(), sales.getCreatedBy().getFullName().getLastName());
        assertEquals(dto.getUser().getFullName().getPatronymic(), sales.getCreatedBy().getFullName().getPatronymic());
        assertEquals(dto.getUserId(), sales.getCreatedById());

        assertEquals(dto.getStatus(), sales.getStatus() == SalesStatus.DRAFT ? ReceiptStatus.DRAFT : sales.getType().getReceiptStatus());
        assertEquals(dto.getSource(), sales.getSource());
        assertEquals(dto.getPromotionId(), sales.getPromotionId());


        assertEquals(dto.getAdditional().getSamModuleSerialNumber(), sales.getAdditional().getSamModuleSerialNumber());
        assertEquals(dto.getAdditional().getReceiptSeq(), sales.getAdditional().getReceiptSeq());
        assertEquals(dto.getAdditional().getFiscalCreatedDate(), sales.getAdditional().getFiscalCreatedDate());
        assertEquals(dto.getAdditional().getProviderId(), sales.getAdditional().getProviderId());
        assertEquals(dto.getAdditional().getTransactionId(), sales.getAdditional().getTransactionId());
        assertEquals(dto.getAdditional().getNumberInShift(), sales.getAdditional().getNumberInShift());
        assertEquals(dto.getAdditional().getEpaymentId(), sales.getAdditional().getEpaymentId());
        assertEquals(dto.getAdditional().getVoucherId(), sales.getAdditional().getVoucherId());
        assertEquals(dto.getAdditional().getCarPlateNumber(), sales.getAdditional().getCarPlateNumber());
        assertEquals(dto.getAdditional().getColumnNumber(), sales.getAdditional().getColumnNumber());
        assertEquals(dto.getAdditional().getCardType(), sales.getAdditional().getCardType());
        assertEquals(dto.getAdditional().getRrn(), sales.getAdditional().getRrn());
        assertEquals(dto.getAdditional().getTerminalVersion(), sales.getAdditional().getTerminalVersion());
        assertEquals(dto.getAdditional().isForCredit(), sales.getAdditional().isForCredit());
        assertEquals(dto.getAdditional().getMonth(), sales.getAdditional().getMonth());
        assertEquals(dto.getAdditional().getHomeServiceAccount(), sales.getAdditional().getHomeServiceAccount());
        assertEquals(dto.getAdditional().getCardPan(), sales.getAdditional().getCardPan());
        assertEquals(dto.getAdditional().getAppType(), sales.getAdditional().getAppType());

        assertEquals(dto.getLat(), sales.getLat());
        assertEquals(dto.getLon(), sales.getLon());
        assertEquals(dto.getCustomerName(), sales.getAdditional().getCustomerName());
        assertEquals(dto.getCustomerPhone(), sales.getAdditional().getCustomerPhone());
        assertEquals(dto.getUid(), sales.getUid());
        assertEquals(dto.getReceiptDateTime(), sales.getReceiptDateTime());
        assertEquals(dto.getFacturaId(), sales.getFacturaId());
        assertEquals(dto.isSendSms(), sales.getAdditional().isSendSms());

    }

    @Test
    void updateFiscalData() {
        Sales sales = Instancio.create(Sales.class);
        SalesFiscalDto fiscalDto = Instancio.create(SalesFiscalDto.class);
        salesMapper.updateFiscalData(sales, fiscalDto);
        assertEquals(sales.getReceiptDateTime(), fiscalDto.getFiscalCreatedDate());
        assertEquals(sales.getAdditional().getSamModuleSerialNumber(), fiscalDto.getSamModuleSerialNumber());
        assertEquals(sales.getAdditional().getReceiptSeq(), fiscalDto.getReceiptSeq());
        assertEquals(sales.getAdditional().getFiscalCreatedDate(), fiscalDto.getFiscalCreatedDate());
        assertEquals(sales.getAdditional().getProviderId(), fiscalDto.getProviderId());
        assertEquals(sales.getAdditional().getTransactionId(), fiscalDto.getTransactionId());
        assertEquals(sales.getAdditional().getNumberInShift(), fiscalDto.getNumberInShift());
        assertEquals(sales.getAdditional().getEpaymentId(), fiscalDto.getEpaymentId());
        assertEquals(sales.getAdditional().getVoucherId(), fiscalDto.getVoucherId());
        assertEquals(sales.getAdditional().getCarPlateNumber(), fiscalDto.getCarPlateNumber());
        assertEquals(sales.getAdditional().getColumnNumber(), fiscalDto.getColumnNumber());
        assertEquals(sales.getAdditional().getCardType(), fiscalDto.getCardType());
        assertEquals(sales.getAdditional().getRrn(), fiscalDto.getRrn());
        assertEquals(sales.getAdditional().getTerminalVersion(), fiscalDto.getTerminalVersion());
        assertEquals(sales.getAdditional().getHomeServiceAccount(), fiscalDto.getHomeServiceAccount());
        assertEquals(sales.getAdditional().getCardPan(), fiscalDto.getCardPan());
        assertEquals(sales.getAdditional().getAppType(), fiscalDto.getAppType());
    }

    @Test
    void toReceipt() {
        Sales sale = Instancio.create(Sales.class);
        Receipt receipt = salesMapper.toReceiptFromSales(sale);
        assertEquals(receipt.getUid(), sale.getUid());
        assertEquals(receipt.getOriginUid(), sale.getOriginUid());
        assertEquals(receipt.getFiscalSign(), sale.getFiscalSign());
        assertEquals(receipt.getFiscalUrl(), sale.getFiscalUrl());
        assertEquals(receipt.getShiftNumber(), sale.getShiftNumber());
        assertEquals(receipt.getLat(), sale.getLat());
        assertEquals(receipt.getLon(), sale.getLon());
        assertEquals(receipt.getReceiptDateTime(), sale.getReceiptDateTime());
        assertEquals(receipt.getTotalCost(), sale.getTotalCost());
        assertEquals(receipt.getTotalPaid(), sale.getTotalPaid());
        assertEquals(receipt.getDiscountPercent(), sale.getDiscountPercent());
        assertEquals(receipt.getCustomerName(), sale.getAdditional().getCustomerName());
        assertEquals(receipt.getCustomerPhone(), sale.getAdditional().getCustomerPhone());
        assertEquals(receipt.getStatus(), sale.getType().getReceiptStatus());
        assertEquals(receipt.getSource(), sale.getSource());
        sale.getPayments().forEach(((paymentType, bigDecimal) -> assertEquals(receipt.getPayments().get(paymentType), bigDecimal)));
        assertEquals(receipt.getBranchId(), sale.getBranchId());
        assertEquals(receipt.getCompanyId(), sale.getCompanyId());
        assertEquals(receipt.getCreatedById(), sale.getCreatedById());
        assertEquals(receipt.getTerminalId(), sale.getTerminal().getId());
        assertEquals(receipt.getCreatedDateTime(), sale.getCreatedAt());

        assertEquals(receipt.getReceiptDetails().size(), sale.getSalesDetails().size());
        Map<Long, SalesDetail> detailMap = sale.getSalesDetails().stream().collect(Collectors.toMap(SalesDetail::getProductId, Function.identity()));
        receipt.getReceiptDetails().forEach(receiptDetail -> {
            var salesDetail = detailMap.get(receiptDetail.getProductId());
            assertEquals(receiptDetail.getReceipt(), receipt);
            assertEquals(receiptDetail.getProductName(), salesDetail.getProductName());
            assertEquals(receiptDetail.getProductBarcode(), salesDetail.getProductBarcode());
            assertEquals(receiptDetail.getVatBarcode(), salesDetail.getVatBarcode());
            assertEquals(receiptDetail.getCommittentTin(), salesDetail.getCommittentTin());
            assertEquals(receiptDetail.getUnitId(), salesDetail.getUnitId());
            assertEquals(receiptDetail.getUnitName(), salesDetail.getUnitName());
            assertEquals(receiptDetail.getCategoryId(), salesDetail.getCategoryId());
            assertEquals(receiptDetail.getCategoryName(), salesDetail.getCategoryName());
            assertEquals(receiptDetail.getPrice(), salesDetail.getPrice());
            assertEquals(receiptDetail.getQty(), salesDetail.getQty());
            assertEquals(receiptDetail.getAmount(), salesDetail.getAmount());
            assertEquals(receiptDetail.getDiscount(), salesDetail.getDiscount());
            assertEquals(receiptDetail.getDiscountPercent(), salesDetail.getDiscountPercent());
            assertEquals(receiptDetail.getNds(), salesDetail.getVat());
            assertEquals(receiptDetail.getNdsPercent(), salesDetail.getVatPercent());
            assertEquals(receiptDetail.getExciseRate(), salesDetail.getExciseRate());
            assertEquals(receiptDetail.getExcise(), salesDetail.getExcise());
            assertEquals(receiptDetail.getMarks(), salesDetail.getMarks());
            assertEquals(receiptDetail.getExtraDetail(), salesDetail.getExtraDetail());
        });

        assertEquals(receipt.getAdditional().getSamModuleSerialNumber(), sale.getAdditional().getSamModuleSerialNumber());
        assertEquals(receipt.getAdditional().getReceiptSeq(), sale.getAdditional().getReceiptSeq());
        assertEquals(receipt.getAdditional().getFiscalCreatedDate(), sale.getAdditional().getFiscalCreatedDate());
        assertEquals(receipt.getAdditional().getProviderId(), sale.getAdditional().getProviderId());
        assertEquals(receipt.getAdditional().getTransactionId(), sale.getAdditional().getTransactionId());
        assertEquals(receipt.getAdditional().getNumberInShift(), sale.getAdditional().getNumberInShift());
        assertEquals(receipt.getAdditional().getEpaymentId(), sale.getAdditional().getEpaymentId());
        assertEquals(receipt.getAdditional().getVoucherId(), sale.getAdditional().getVoucherId());
        assertEquals(receipt.getAdditional().getCarPlateNumber(), sale.getAdditional().getCarPlateNumber());
        assertEquals(receipt.getAdditional().getColumnNumber(), sale.getAdditional().getColumnNumber());
        assertEquals(receipt.getAdditional().getCardType(), sale.getAdditional().getCardType());
        assertEquals(receipt.getAdditional().getRrn(), sale.getAdditional().getRrn());
        assertEquals(receipt.getAdditional().getTerminalVersion(), sale.getAdditional().getTerminalVersion());
        assertEquals(receipt.getAdditional().isForCredit(), sale.getAdditional().isForCredit());
        assertEquals(receipt.getAdditional().getMonth(), sale.getAdditional().getMonth());
        assertEquals(receipt.getAdditional().getHomeServiceAccount(), sale.getAdditional().getHomeServiceAccount());
        assertEquals(receipt.getAdditional().getCardPan(), sale.getAdditional().getCardPan());
        assertEquals(receipt.getAdditional().getAppType(), sale.getAdditional().getAppType());

        assertEquals(receipt.getBillId(), sale.getAdditional().getBillId());
        assertEquals(receipt.getFacturaId(), sale.getFacturaId());
        assertEquals(receipt.isSendSms(), sale.getAdditional().isSendSms());
    }

    @Test
    void getPayments_When_PaymentsIsEmpty() {
        Receipt receipt = new Receipt();
        receipt.setTotalCash(Instancio.create(BigDecimal.class));
        receipt.setTotalCard(Instancio.create(BigDecimal.class));
        receipt.setTotalNds(Instancio.create(BigDecimal.class));
        receipt.setTotalExcise(Instancio.create(BigDecimal.class));
        receipt.setTotalDiscount(Instancio.create(BigDecimal.class));
        Map<PaymentType, BigDecimal> payments = salesMapper.getPayments(receipt);

        assertEquals(receipt.getTotalCash(), payments.get(PaymentType.CASH));
        assertEquals(receipt.getTotalCard(), payments.get(PaymentType.CARD));
        assertEquals(receipt.getTotalNds(), payments.get(PaymentType.NDS));
        assertEquals(receipt.getTotalExcise(), payments.get(PaymentType.EXCISE));
        assertEquals(receipt.getTotalDiscount(), payments.get(PaymentType.DISCOUNT));
    }

    @Test
    void getPayments_When_TotalValuesIsExistAlsoPaymentsIsNotEmpty() {
        Receipt receipt = new Receipt();
        receipt.setTotalCash(Instancio.create(BigDecimal.class));
        receipt.setTotalCard(Instancio.create(BigDecimal.class));
        receipt.setTotalNds(Instancio.create(BigDecimal.class));
        receipt.setTotalExcise(Instancio.create(BigDecimal.class));
        receipt.setTotalDiscount(Instancio.create(BigDecimal.class));
        receipt.setPayments(Instancio.createMap(PaymentType.class, BigDecimal.class));
        Map<PaymentType, BigDecimal> payments = salesMapper.getPayments(receipt);

        assertEquals(receipt.getTotalCash(), payments.get(PaymentType.CASH));
        assertEquals(receipt.getTotalCard(), payments.get(PaymentType.CARD));
        assertEquals(receipt.getTotalNds(), payments.get(PaymentType.NDS));
        assertEquals(receipt.getTotalExcise(), payments.get(PaymentType.EXCISE));
        assertEquals(receipt.getTotalDiscount(), payments.get(PaymentType.DISCOUNT));
    }

    @Test
    void getPayments_When_Mixed() {
        Receipt receipt = new Receipt();
        receipt.setTotalCash(BigDecimal.ZERO);
        receipt.setTotalCard(Instancio.create(BigDecimal.class));
        receipt.setTotalExcise(Instancio.create(BigDecimal.class));
        receipt.setTotalDiscount(Instancio.create(BigDecimal.class));
        Map<PaymentType, BigDecimal> receiptPayments = Stream.of(PaymentType.CASH, PaymentType.CARD, PaymentType.NDS, PaymentType.EXCISE, PaymentType.DISCOUNT).collect(Collectors.toMap(Function.identity(), paymentType -> Instancio.create(BigDecimal.class)));
        receipt.setPayments(receiptPayments);
        Map<PaymentType, BigDecimal> payments = salesMapper.getPayments(receipt);

        assertEquals(receiptPayments.get(PaymentType.CASH), payments.get(PaymentType.CASH));
        assertEquals(receipt.getTotalCard(), payments.get(PaymentType.CARD));
        assertEquals(receiptPayments.get(PaymentType.NDS), payments.get(PaymentType.NDS));
        assertEquals(receipt.getTotalExcise(), payments.get(PaymentType.EXCISE));
        assertEquals(receipt.getTotalDiscount(), payments.get(PaymentType.DISCOUNT));
    }

    @Test
    void toSalesDtoFromReceipt() {
        Receipt receipt = Instancio.create(Receipt.class);
        SalesDto salesDto = salesMapper.toSalesDtoFromReceipt(receipt);
        assertEquals(salesDto.getUid(), receipt.getUid());
        assertEquals(salesDto.getOriginUid(), receipt.getOriginUid());
        assertEquals(salesDto.getFiscalSign(), receipt.getFiscalSign());
        assertEquals(salesDto.getFiscalUrl(), receipt.getFiscalUrl());
        assertEquals(salesDto.getCreatedDateTime(), receipt.getCreatedDateTime());
        assertEquals(salesDto.getPayments(), salesMapper.getPayments(receipt));
        assertEquals(salesDto.getTotalCost(), receipt.getTotalCost());
        assertEquals(salesDto.getDiscountPercent(), receipt.getDiscountPercent());
        assertEquals(salesDto.getTotalPaid(), receipt.getTotalPaid());
        assertEquals(salesDto.getBillId(), receipt.getBillId());
        assertEquals(salesDto.getBranchId(), receipt.getBranchId());
        assertEquals(salesDto.getBranchName(), receipt.getBranch().getName());
        assertEquals(salesDto.getLat(), receipt.getLat());
        assertEquals(salesDto.getLon(), receipt.getLon());
        assertEquals(salesDto.getTerminalSN(), receipt.getTerminal().getSerialNumber());
        assertEquals(salesDto.getCompanyId(), receipt.getCompanyId());
        assertEquals(salesDto.getCompanyName(), receipt.getCompany().getName());
        assertEquals(salesDto.getCompanyINN(), receipt.getCompany().getInn());
        assertEquals(salesDto.getShiftNo(), receipt.getShiftNumber());

        assertEquals(salesDto.getReceiptDetails().size(), receipt.getReceiptDetails().size());
        Map<Long, ReceiptDetail> receiptDetailMap = receipt.getReceiptDetails().stream().collect(Collectors.toMap(ReceiptDetail::getProductId, Function.identity()));
        salesDto.getReceiptDetails().forEach(salesDetailDto -> {
            ReceiptDetail receiptDetail = receiptDetailMap.get(salesDetailDto.getProductId());
            assertEquals(salesDetailDto.getProductId(), receiptDetail.getProductId());
            assertEquals(salesDetailDto.getProductName(), receiptDetail.getProductName());
            assertEquals(salesDetailDto.getProductBarcode(), receiptDetail.getProductBarcode());
            assertEquals(salesDetailDto.getVatBarcode(), receiptDetail.getVatBarcode());
            assertEquals(salesDetailDto.getUnitId(), receiptDetail.getUnitId());
            assertEquals(salesDetailDto.getUnitName(), receiptDetail.getUnitName());
            assertEquals(salesDetailDto.getExcise(), receiptDetail.getExcise());
            assertEquals(salesDetailDto.getExciseRate(), receiptDetail.getExciseRate());
            assertEquals(salesDetailDto.getPrice(), receiptDetail.getPrice());
            assertEquals(salesDetailDto.getQty(), receiptDetail.getQty());
            assertEquals(salesDetailDto.getAmount(), receiptDetail.getAmount());
            assertEquals(salesDetailDto.getDiscountPercent(), receiptDetail.getDiscountPercent());
            assertEquals(salesDetailDto.getDiscount(), receiptDetail.getDiscount());
            assertEquals(salesDetailDto.getNdsPercent(), receiptDetail.getNdsPercent());
            assertEquals(salesDetailDto.getNds(), receiptDetail.getNds());
            assertEquals(salesDetailDto.getMarks(), receiptDetail.getMarks());
            assertEquals(salesDetailDto.getCommittentTin(), receiptDetail.getCommittentTin());
            assertEquals(salesDetailDto.getCategoryId(), receiptDetail.getCategoryId());
            assertEquals(salesDetailDto.getCategoryName(), receiptDetail.getCategoryName());
            assertEquals(salesDetailDto.getExtraDetail(), receiptDetail.getExtraDetail());
        });

        assertEquals(salesDto.getUser().getId(), receipt.getCreatedBy().getId());
        assertEquals(salesDto.getUser().getName(), receipt.getCreatedBy().getFullNameStr());
        assertEquals(salesDto.getType(), receipt.getStatus().getSalesType());
        assertEquals(salesDto.getStatus(), receipt.getStatus() == ReceiptStatus.DRAFT ? SalesStatus.DRAFT : SalesStatus.COMPLETED);
        assertEquals(salesDto.getSource(), receipt.getSource());
        assertEquals(salesDto.getAdditional(), receipt.getAdditional());
        assertEquals(salesDto.getCustomerName(), receipt.getCustomerName());
        assertEquals(salesDto.getCustomerPhone(), receipt.getCustomerPhone());
        assertEquals(salesDto.getReceiptDateTime(), receipt.getReceiptDateTime());
        assertEquals(salesDto.getFacturaId(), receipt.getFacturaId());
    }

    @Test
    void updateCompletedSaleFromReceipt() {
        Receipt receipt = Instancio.create(Receipt.class);
        Sales sales = Instancio.create(Sales.class);
        sales = salesMapper.updateCompletedSaleFromReceipt(sales, receipt);
        assertEquals(SalesStatus.COMPLETED, sales.getStatus());

        assertEquals(sales.getReceiptDateTime(), receipt.getReceiptDateTime());
        assertEquals(sales.getAdditional().getSamModuleSerialNumber(), receipt.getAdditional().getSamModuleSerialNumber());
        assertEquals(sales.getAdditional().getReceiptSeq(), receipt.getAdditional().getReceiptSeq());
        assertEquals(sales.getAdditional().getFiscalCreatedDate(), receipt.getAdditional().getFiscalCreatedDate());
        assertEquals(sales.getAdditional().getProviderId(), receipt.getAdditional().getProviderId());
        assertEquals(sales.getAdditional().getTransactionId(), receipt.getAdditional().getTransactionId());
        assertEquals(sales.getAdditional().getNumberInShift(), receipt.getAdditional().getNumberInShift());
        assertEquals(sales.getAdditional().getEpaymentId(), receipt.getAdditional().getEpaymentId());
        assertEquals(sales.getAdditional().getVoucherId(), receipt.getAdditional().getVoucherId());
        assertEquals(sales.getAdditional().getCarPlateNumber(), receipt.getAdditional().getCarPlateNumber());
        assertEquals(sales.getAdditional().getColumnNumber(), receipt.getAdditional().getColumnNumber());
        assertEquals(sales.getAdditional().getCardType(), receipt.getAdditional().getCardType());
        assertEquals(sales.getAdditional().getRrn(), receipt.getAdditional().getRrn());
        assertEquals(sales.getAdditional().getTerminalVersion(), receipt.getAdditional().getTerminalVersion());
        assertEquals(sales.getAdditional().getHomeServiceAccount(), receipt.getAdditional().getHomeServiceAccount());
        assertEquals(sales.getAdditional().getCardPan(), receipt.getAdditional().getCardPan());
        assertEquals(sales.getAdditional().getAppType(), receipt.getAdditional().getAppType());

        assertEquals(sales.getAdditional().isForCredit(), receipt.getAdditional().isForCredit());
        assertEquals(sales.getAdditional().getMonth(), receipt.getAdditional().getMonth());
    }

    @Test
    void toSalesFromReceipt() {
        Receipt receipt = Instancio.create(Receipt.class);
        var sales = salesMapper.toSalesFromReceipt(receipt);
        assertNull(sales.getId());
        assertEquals(sales.getCreatedDateTime(), receipt.getCreatedDateTime());
        assertEquals(sales.getReceiptDateTime(), receipt.getReceiptDateTime());
        assertEquals(sales.getUid(), receipt.getUid());
        assertEquals(sales.getOriginUid(), receipt.getOriginUid());
        assertEquals(sales.getFiscalSign(), receipt.getFiscalSign());
        assertEquals(sales.getFiscalUrl(), receipt.getFiscalUrl());
        assertEquals(sales.getShiftNumber(), receipt.getShiftNumber());
        assertEquals(sales.getLat(), receipt.getLat());
        assertEquals(sales.getLon(), receipt.getLon());
        assertEquals(sales.getTotalCost(), receipt.getTotalCost());
        assertEquals(sales.getTotalPaid(), receipt.getTotalPaid());
        assertEquals(sales.getDiscountPercent(), receipt.getDiscountPercent());
        assertEquals(sales.getType(), receipt.getStatus().getSalesType());
        assertEquals(sales.getStatus(), receipt.getStatus() == ReceiptStatus.DRAFT ? SalesStatus.DRAFT : SalesStatus.COMPLETED);
        assertEquals(sales.getSource(), receipt.getSource());
        assertEquals(sales.getPayments(), salesMapper.getPayments(receipt));
        assertEquals(sales.getBranchId(), receipt.getBranchId());
        assertEquals(sales.getCompanyId(), receipt.getCompanyId());
        assertEquals(sales.getCreatedById(), receipt.getCreatedById());
        assertEquals(sales.getTerminalSN(), receipt.getTerminal().getSerialNumber());
        assertEquals(sales.getAdditional().getSamModuleSerialNumber(), receipt.getAdditional().getSamModuleSerialNumber());
        assertEquals(sales.getAdditional().getReceiptSeq(), receipt.getAdditional().getReceiptSeq());
        assertEquals(sales.getAdditional().getFiscalCreatedDate(), receipt.getAdditional().getFiscalCreatedDate());
        assertEquals(sales.getAdditional().getProviderId(), receipt.getAdditional().getProviderId());
        assertEquals(sales.getAdditional().getTransactionId(), receipt.getAdditional().getTransactionId());
        assertEquals(sales.getAdditional().getNumberInShift(), receipt.getAdditional().getNumberInShift());
        assertEquals(sales.getAdditional().getEpaymentId(), receipt.getAdditional().getEpaymentId());
        assertEquals(sales.getAdditional().getVoucherId(), receipt.getAdditional().getVoucherId());
        assertEquals(sales.getAdditional().getCarPlateNumber(), receipt.getAdditional().getCarPlateNumber());
        assertEquals(sales.getAdditional().getColumnNumber(), receipt.getAdditional().getColumnNumber());
        assertEquals(sales.getAdditional().getCardType(), receipt.getAdditional().getCardType());
        assertEquals(sales.getAdditional().getRrn(), receipt.getAdditional().getRrn());
        assertEquals(sales.getAdditional().getTerminalVersion(), receipt.getAdditional().getTerminalVersion());
        assertEquals(sales.getAdditional().isForCredit(), receipt.getAdditional().isForCredit());
        assertEquals(sales.getAdditional().getMonth(), receipt.getAdditional().getMonth());
        assertEquals(sales.getAdditional().getHomeServiceAccount(), receipt.getAdditional().getHomeServiceAccount());
        assertEquals(sales.getAdditional().getCardPan(), receipt.getAdditional().getCardPan());
        assertEquals(sales.getAdditional().getAppType(), receipt.getAdditional().getAppType());
        assertEquals(sales.getAdditional().getCustomerName(), receipt.getCustomerName());
        assertEquals(sales.getAdditional().getCustomerPhone(), receipt.getCustomerPhone());
        assertEquals(sales.getAdditional().getBillId(), receipt.getBillId());
        assertEquals(sales.getAdditional().isSendSms(), receipt.isSendSms());
        assertEquals(sales.getFacturaId(), receipt.getFacturaId());

        assertEquals(sales.getSalesDetails().size(), receipt.getReceiptDetails().size());
        Map<Long, ReceiptDetail> receiptDetailMap = receipt.getReceiptDetails().stream().collect(Collectors.toMap(ReceiptDetail::getProductId, Function.identity()));
        sales.getSalesDetails().forEach(salesDetail -> {
            ReceiptDetail receiptDetail = receiptDetailMap.get(salesDetail.getProductId());
            assertEquals(salesDetail.getProductId(), receiptDetail.getProductId());
            assertEquals(salesDetail.getProductName(), receiptDetail.getProductName());
            assertEquals(salesDetail.getProductBarcode(), receiptDetail.getProductBarcode());
            assertEquals(salesDetail.getVatBarcode(), receiptDetail.getVatBarcode());
            assertEquals(salesDetail.getUnitId(), receiptDetail.getUnitId());
            assertEquals(salesDetail.getUnitName(), receiptDetail.getUnitName());
            assertEquals(salesDetail.getExcise(), receiptDetail.getExcise());
            assertEquals(salesDetail.getExciseRate(), receiptDetail.getExciseRate());
            assertEquals(salesDetail.getPrice(), receiptDetail.getPrice());
            assertEquals(salesDetail.getQty(), receiptDetail.getQty());
            assertEquals(salesDetail.getAmount(), receiptDetail.getAmount());
            assertEquals(salesDetail.getDiscountPercent(), receiptDetail.getDiscountPercent());
            assertEquals(salesDetail.getDiscount(), receiptDetail.getDiscount());
            assertEquals(salesDetail.getVatPercent(), receiptDetail.getNdsPercent());
            assertEquals(salesDetail.getVat(), receiptDetail.getNds());
            assertEquals(salesDetail.getMarks(), receiptDetail.getMarks());
            assertEquals(salesDetail.getCommittentTin(), receiptDetail.getCommittentTin());
            assertEquals(salesDetail.getCategoryId(), receiptDetail.getCategoryId());
            assertEquals(salesDetail.getCategoryName(), receiptDetail.getCategoryName());
            assertEquals(salesDetail.getExtraDetail(), receiptDetail.getExtraDetail());
        });
    }
}