package uz.uzkassa.smartpos.mappers.clickhouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.dto.CreditReceiptDTO;
import uz.uzkassa.smartpos.dto.CreditReceiptResponseDTO;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceipt;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceiptDetail;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClickhouseReceiptMapper {
    private final ObjectMapper objectMapper;

    public ClickhouseReceiptMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ClickhouseReceipt toClickhouseReceipt(Receipt receipt, List<Long> receiptDetailIds) {
        ClickhouseReceipt receiptDTO = new ClickhouseReceipt();

        receiptDTO.setId(receipt.getId());
        receiptDTO.setUid(receipt.getUid());
        receiptDTO.setOriginUid(receipt.getOriginUid());
        receiptDTO.setFiscalSign(receipt.getFiscalSign());
        receiptDTO.setFiscalUrl(receipt.getFiscalUrl());
        receiptDTO.setLat(receipt.getLat());
        receiptDTO.setLon(receipt.getLon());
        receiptDTO.setReceiptDateTime(ClickhouseUtils.dateTimeFormatter.format(receipt.getReceiptDateTime()));
        receiptDTO.setTotalCash(receipt.getTotalCash());
        receiptDTO.setTotalCard(receipt.getTotalCard());
        receiptDTO.setTotalCost(receipt.getTotalCost());
        receiptDTO.setTotalPaid(receipt.getTotalPaid());
        receiptDTO.setTotalDiscount(receipt.getTotalDiscount());
        receiptDTO.setTotalExcise(receipt.getTotalExcise());
        receiptDTO.setDiscountPercent(receipt.getDiscountPercent());
        receiptDTO.setTotalNds(receipt.getTotalNds());
        receiptDTO.setShiftNumber(receipt.getShiftNumber());
        receiptDTO.setCustomerName(receipt.getCustomerName());
        receiptDTO.setCustomerPhone(receipt.getCustomerPhone());
        receiptDTO.setReadOnly((byte) (receipt.isReadOnly() ? 1 : 0));
        receiptDTO.setForceToPrint((byte) (receipt.isForceToPrint() ? 1 : 0));
        receiptDTO.setStatus(receipt.getStatus().name());
        receiptDTO.setSource(receipt.getSource() != null ? receipt.getSource().name() : null);
        receiptDTO.setPayments(receipt.getPayments());

        receiptDTO.setTerminalId(receipt.getTerminalId());
        if (receipt.getTerminal() != null) {
            receiptDTO.setTerminalSerialNumber(receipt.getTerminal().getSerialNumber());
        }

        receiptDTO.setBranchId(receipt.getBranchId());
        if (receipt.getBranch() != null) {
            receiptDTO.setBranchName(receipt.getBranch().getName());
            receiptDTO.setBranchCompanyId(receipt.getBranch().getCompanyId());
            receiptDTO.setBranchActivityTypeId(receipt.getBranch().getActivityTypeId());
            receiptDTO.setBranchRegionId(receipt.getBranch().getRegionId());
            receiptDTO.setBranchCityId(receipt.getBranch().getCityId());
            if (receipt.getTerminal() == null) {
                receiptDTO.setTerminalSerialNumber(receipt.getBranch().getSerialNumber());
            }
        }

        receiptDTO.setCompanyId(receipt.getCompanyId());
        if (receipt.getCompany() != null) {
            receiptDTO.setCompanyInn(receipt.getCompany().getInn());
            receiptDTO.setCompanyName(receipt.getCompany().getName());
            receiptDTO.setCompanyRegionId(receipt.getCompany().getRegionId());
            receiptDTO.setCompanyCityId(receipt.getCompany().getCityId());
        }

        receiptDTO.setCreatedById(receipt.getCreatedById());
        if (receipt.getCreatedBy() != null) {
            receiptDTO.setCreatedByFirstname(receipt.getCreatedBy().getFirstName());
            receiptDTO.setCreatedByLastname(receipt.getCreatedBy().getLastName());
        }

        receiptDTO.setCreatedDateTime(ClickhouseUtils.dateTimeFormatter.format(receipt.getCreatedDateTime()));
        receiptDTO.setReceiptDetailIds(receiptDetailIds);
        try {
            receiptDTO.setAdditional(objectMapper.writeValueAsString(receipt.getAdditional()));
        } catch (JsonProcessingException ignored) {
        }
        return receiptDTO;
    }

    public ClickhouseReceiptDetail toClickhouseReceiptDetail(ReceiptDetail receiptDetail, ClickhouseReceipt receipt) {
        ClickhouseReceiptDetail dto = new ClickhouseReceiptDetail();
        dto.setMarks(receiptDetail.getMarks());
        dto.setId(receiptDetail.getId());
        dto.setProductId(receiptDetail.getProductId());
        dto.setProductName(receiptDetail.getProductName());
        dto.setProductBarcode(receiptDetail.getProductBarcode());
        dto.setVatBarcode(receiptDetail.getVatBarcode());
        dto.setUnitId(receiptDetail.getUnitId());
        dto.setUnitName(receiptDetail.getUnitName());
        dto.setCategoryId(receiptDetail.getCategoryId());
        dto.setCategoryName(receiptDetail.getCategoryName());
        dto.setPrice(receiptDetail.getPrice());
        dto.setQty(receiptDetail.getQty());
        dto.setAmount(receiptDetail.getAmount());
        dto.setNds(receiptDetail.getNds());
        dto.setNdsPercent(receiptDetail.getNdsPercent());
        dto.setDiscount(receiptDetail.getDiscount());
        dto.setDiscountPercent(receiptDetail.getDiscountPercent());
        dto.setExcise(receiptDetail.getExcise());
        dto.setExciseRate(receiptDetail.getExciseRate());
        dto.setReceiptId(receiptDetail.getReceiptId());
        dto.setBranchId(receipt.getBranchId());
        dto.setCommittentTin(receiptDetail.getCommittentTin());
        dto.setReceiptDateTime(ClickhouseUtils.dateTimeFormatter.format(receiptDetail.getReceiptDateTime()));
        dto.setStatus(receipt.getStatus());
        dto.setCreatedById(receipt.getCreatedById());
        dto.setCompanyId(receipt.getCompanyId());
        dto.setBranchName(receipt.getBranchName());
        dto.setCommittentTin(receiptDetail.getCommittentTin());
        try {
            dto.setExtraDetail(objectMapper.writeValueAsString(receiptDetail.getExtraDetail()));
        } catch (Exception ignored) {
        }
        return dto;
    }

    public List<ReceiptDTO> toReceiptListDTO(List<ClickhouseReceipt> receipt, List<ClickhouseReceiptDetail> receiptDetailList, boolean noFiscal) {
        Map<Long, List<ClickhouseReceiptDetail>> receiptDetailsMap = receiptDetailList.stream().collect(Collectors.groupingBy(ClickhouseReceiptDetail::getReceiptId, Collectors.toList()));
        return receipt.stream().map(clickhouseReceipt -> toReceiptDTO(clickhouseReceipt, receiptDetailsMap.getOrDefault(clickhouseReceipt.getId(), List.of()), noFiscal)).toList();
    }


    public Optional<ReceiptDTO> toReceiptDTO(Optional<ClickhouseReceipt> receipt, List<ClickhouseReceiptDetail> receiptDetailList, boolean noFiscal) {
        return receipt.map(clickhouseReceipt -> toReceiptDTO(clickhouseReceipt, receiptDetailList, noFiscal));
    }

    public ReceiptDTO toReceiptDTO(ClickhouseReceipt receipt, List<ClickhouseReceiptDetail> receiptDetailList, boolean noFiscal) {
        ReceiptDTO dto = new ReceiptDTO();
        dto.setId(receipt.getId());
        dto.setLat(receipt.getLat());
        dto.setLon(receipt.getLon());
        dto.setCustomerName(receipt.getCustomerName());
        dto.setCustomerPhone(receipt.getCustomerPhone());
        dto.setReadOnly(receipt.getReadOnly() == 1);
        dto.setForceToPrint(receipt.getForceToPrint() == 1);
        dto.setReceiptDateTime(LocalDateTime.parse(receipt.getReceiptDateTime(), ClickhouseUtils.dateTimeFormatter));
        dto.setOriginUid(receipt.getOriginUid());
        if (!noFiscal) {
            dto.setUid(receipt.getUid());
            dto.setFiscalSign(receipt.getFiscalSign());
            dto.setFiscalUrl(receipt.getFiscalUrl());
        }
        dto.setCreatedDateTime(LocalDateTime.parse(receipt.getCreatedDateTime(), ClickhouseUtils.dateTimeFormatter));
        dto.setPayments(receipt.getPayments());
        dto.setTotalCash(receipt.getTotalCash());
        dto.setTotalCard(receipt.getTotalCard());
        dto.setTotalLoyaltyCard(receipt.getTotalLoyaltyCard());
        dto.setTotalCost(receipt.getTotalCost());
        dto.setTotalDiscount(receipt.getTotalDiscount());
        dto.setTotalExcise(receipt.getTotalExcise());
        dto.setDiscountPercent(receipt.getDiscountPercent());
        dto.setTotalPaid(receipt.getTotalPaid());
        dto.setTotalNds(receipt.getTotalNds());
        dto.setLoyaltyCardId(receipt.getLoyaltyCardId());
        dto.setCustomerId(receipt.getCustomerId());
        dto.setBranchId(receipt.getBranchId());
        dto.setBranchName(receipt.getBranchName());
        dto.setTerminalId(receipt.getTerminalId());
        dto.setTerminalSN(receipt.getTerminalSerialNumber());
        dto.setCompanyId(receipt.getCompanyId());
        dto.setCompanyName(receipt.getCompanyName());
        dto.setCompanyINN(receipt.getCompanyInn());
        dto.setShiftNo(receipt.getShiftNumber());
        dto.setReceiptDetails(new LinkedList<>(receiptDetailList.stream().map(this::toReceiptDetailDTO).collect(Collectors.toList())));
        UserDTO user = new UserDTO();
        user.setId(receipt.getCreatedById());
        user.setFullName(new FullNameDTO(receipt.getCreatedByFirstname(), receipt.getCreatedByLastname(), null));
        dto.setUser(user);
        dto.setStatus(ReceiptStatus.valueOf(receipt.getStatus()));
        try {
            dto.setAdditional(objectMapper.readValue(receipt.getAdditional(), Additional.class));
        } catch (IOException ignored) {
        }
        return dto;
    }

    public ReceiptDetailDTO toReceiptDetailDTO(ClickhouseReceiptDetail receiptDetail) {
        ReceiptDetailDTO dto = new ReceiptDetailDTO();
        dto.setProductId(receiptDetail.getProductId());
        dto.setProductName(receiptDetail.getProductName());
        dto.setProductBarcode(receiptDetail.getProductBarcode());
        dto.setVatBarcode(receiptDetail.getVatBarcode());
        dto.setUnitId(receiptDetail.getUnitId());
        dto.setUnitName(receiptDetail.getUnitName());
        dto.setCategoryId(receiptDetail.getCategoryId());
        dto.setCategoryName(receiptDetail.getCategoryName());
        dto.setPrice(receiptDetail.getPrice());
        dto.setQty(receiptDetail.getQty());
        dto.setAmount(receiptDetail.getAmount());
        dto.setNds(receiptDetail.getNds());
        dto.setNdsPercent(receiptDetail.getNdsPercent());
        dto.setDiscount(receiptDetail.getDiscount());
        dto.setDiscountPercent(receiptDetail.getDiscountPercent());
        dto.setExcise(receiptDetail.getExcise());
        dto.setExciseRate(receiptDetail.getExciseRate());
        dto.setReceiptId(receiptDetail.getReceiptId());
        dto.setBranchId(receiptDetail.getBranchId());
        dto.setMarks(receiptDetail.getMarks());
        dto.setCommittentTin(receiptDetail.getCommittentTin());
        if (StringUtils.isNotBlank(receiptDetail.getExtraDetail())) {
            try {
                dto.setExtraDetail(objectMapper.readValue(receiptDetail.getExtraDetail(), ExtraDetail.class));
            } catch (IOException ignored) {
            }
        }
        return dto;
    }

    public CreditReceiptResponseDTO toCreditReceipts(ClickhouseReceipt receipt, List<CreditReceiptDTO> child, List<ClickhouseReceiptDetail> receiptDetailList) {
        CreditReceiptResponseDTO dto = new CreditReceiptResponseDTO();
        dto.setId(receipt.getId());
        dto.setLat(receipt.getLat());
        dto.setLon(receipt.getLon());
        dto.setCustomerName(receipt.getCustomerName());
        dto.setCustomerPhone(receipt.getCustomerPhone());
        dto.setReadOnly(receipt.getReadOnly() == 1);
        dto.setForceToPrint(receipt.getForceToPrint() == 1);
        dto.setUid(receipt.getUid());
        dto.setReceiptDateTime(LocalDateTime.parse(receipt.getReceiptDateTime(), ClickhouseUtils.dateTimeFormatter));
        dto.setOriginUid(receipt.getOriginUid());
        dto.setFiscalSign(receipt.getFiscalSign());
        dto.setFiscalUrl(receipt.getFiscalUrl());
        dto.setCreatedDateTime(LocalDateTime.parse(receipt.getCreatedDateTime(), ClickhouseUtils.dateTimeFormatter));
        dto.setPayments(receipt.getPayments());
        dto.setTotalCash(receipt.getTotalCash());
        dto.setTotalCard(receipt.getTotalCard());
        dto.setTotalLoyaltyCard(receipt.getTotalLoyaltyCard());
        dto.setTotalCost(receipt.getTotalCost());
        dto.setTotalDiscount(receipt.getTotalDiscount());
        dto.setTotalExcise(receipt.getTotalExcise());
        dto.setDiscountPercent(receipt.getDiscountPercent());
        dto.setTotalPaid(child.stream().map(CreditReceiptDTO::getTotalPaid).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTotalNds(receipt.getTotalNds());
        dto.setLoyaltyCardId(receipt.getLoyaltyCardId());
        dto.setCustomerId(receipt.getCustomerId());
        dto.setBranchId(receipt.getBranchId());
        dto.setBranchName(receipt.getBranchName());
        dto.setTerminalId(receipt.getTerminalId());
        dto.setTerminalSN(receipt.getTerminalSerialNumber());
        dto.setCompanyId(receipt.getCompanyId());
        dto.setCompanyName(receipt.getCompanyName());
        dto.setCompanyINN(receipt.getCompanyInn());
        dto.setShiftNo(receipt.getShiftNumber());
        dto.setReceiptDetails(new LinkedList<>(receiptDetailList.stream().map(this::toReceiptDetailDTO).collect(Collectors.toList())));
        UserDTO user = new UserDTO();
        user.setId(receipt.getCreatedById());
        user.setFullName(new FullNameDTO(receipt.getCreatedByFirstname(), receipt.getCreatedByLastname(), null));
        dto.setUser(user);
        dto.setStatus(ReceiptStatus.valueOf(receipt.getStatus()));
        try {
            dto.setAdditional(objectMapper.readValue(receipt.getAdditional(), Additional.class));
        } catch (IOException ignored) {
        }
        dto.setChild(child);
        return dto;
    }
}
