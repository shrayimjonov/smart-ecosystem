package uz.uzkassa.smartpos.mappers.clickhouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceipt;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.service.BeanUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickhouseReceiptRowMappers {
    public static final RowMapper<ReceiptDTO> RECEIPT_DTO = (rs, rowNum) -> {
        var receiptDTO = new ReceiptDTO();
        receiptDTO.setId(rs.getLong("id"));
        receiptDTO.setUid(rs.getString("uid"));
        receiptDTO.setOriginUid(rs.getString("originUid"));
        receiptDTO.setReceiptDateTime(LocalDateTime.parse(rs.getString("receiptDateTime"), ClickhouseUtils.dateTimeFormatter));
        receiptDTO.setTotalCard(rs.getBigDecimal("totalCard"));
        receiptDTO.setTotalNds(rs.getBigDecimal("totalNds"));
        receiptDTO.setShiftNo(rs.getLong("shiftNumber"));

        receiptDTO.setTerminalSN(rs.getString("terminalSerialNumber"));

        receiptDTO.setTotalCash(rs.getBigDecimal("totalCash"));
        Map payment = rs.getObject("payments", Map.class);
        if (payment != null) {
            Map<PaymentType, BigDecimal> payments = ((Map<String, BigDecimal>) payment)
                    .entrySet().stream().collect(Collectors.toMap(e -> PaymentType.valueOf(e.getKey()), Map.Entry::getValue));
            receiptDTO.setPayments(payments);
        }

        receiptDTO.setFiscalSign(rs.getString("fiscalSign"));
        receiptDTO.setFiscalUrl(rs.getString("fiscalUrl"));

        receiptDTO.setBranchId(rs.getLong("branchId"));
        receiptDTO.setBranchName(rs.getString("branchName"));

        receiptDTO.setCompanyName(rs.getString("companyName"));

        receiptDTO.setCustomerName(rs.getString("customerName"));
        receiptDTO.setCustomerPhone(rs.getString("customerPhone"));

        var userDTO = new UserDTO();
        userDTO.setFullName(new FullNameDTO(rs.getString("createdByFirstname"), rs.getString("createdByLastname"), null));
        receiptDTO.setUser(userDTO);

        receiptDTO.setPositionCount(rs.getInt("positionCount"));

        receiptDTO.setStatus(ReceiptStatus.valueOf(rs.getString("status")));
        receiptDTO.setTotalCost(rs.getBigDecimal("totalCost"));
        try {
            String additional = rs.getString("additional");
            if (StringUtils.isNotBlank(additional)) {
                receiptDTO.setAdditional(BeanUtil.getBean(ObjectMapper.class).readValue(additional, Additional.class));
            }
        } catch (IOException ignored) {
        }
        return receiptDTO;
    };

    public static final RowMapper<ClickhouseReceipt> CLICKHOUSE_RECEIPT = (rs, rowNum) -> {
        ClickhouseReceipt receipt = new ClickhouseReceipt();
        receipt.setId(rs.getLong("id"));
        receipt.setUid(rs.getString("uid"));
        receipt.setOriginUid(rs.getString("originUid"));
        receipt.setFiscalSign(rs.getString("fiscalSign"));
        receipt.setFiscalUrl(rs.getString("fiscalUrl"));
        receipt.setLat(rs.getDouble("lat"));
        receipt.setLon(rs.getDouble("lon"));
        receipt.setReceiptDateTime(rs.getString("receiptDateTime"));
        receipt.setTotalCash(rs.getBigDecimal("totalCash"));
        receipt.setTotalCard(rs.getBigDecimal("totalCard"));
        receipt.setTotalLoyaltyCard(rs.getBigDecimal("totalLoyaltyCard"));
        receipt.setTotalCost(rs.getBigDecimal("totalCost"));
        receipt.setTotalPaid(rs.getBigDecimal("totalPaid"));
        receipt.setTotalDiscount(rs.getBigDecimal("totalDiscount"));
        receipt.setTotalExcise(rs.getBigDecimal("totalExcise"));
        receipt.setDiscountPercent(rs.getBigDecimal("discountPercent"));
        receipt.setTotalNds(rs.getBigDecimal("totalNds"));
        receipt.setLoyaltyCardId(rs.getLong("loyaltyCardId"));
        receipt.setShiftNumber(rs.getLong("shiftNumber"));
        receipt.setCustomerId(rs.getLong("customerId"));
        receipt.setCustomerName(rs.getString("customerName"));
        receipt.setCustomerPhone(rs.getString("customerPhone"));
        receipt.setReadOnly(rs.getByte("readOnly"));
        receipt.setForceToPrint(rs.getByte("forceToPrint"));
        receipt.setStatus(rs.getString("status"));
        receipt.setPayments(
                ((Map<String, BigDecimal>) rs.getObject("payments", Map.class)).entrySet().stream()
                        .collect(Collectors.toMap(e -> PaymentType.valueOf(e.getKey()), Map.Entry::getValue))
        );

        receipt.setBranchId(rs.getLong("branchId"));
        receipt.setBranchName(rs.getString("branchName"));
        receipt.setBranchCompanyId(rs.getLong("branchCompanyId"));
        receipt.setBranchActivityTypeId(rs.getLong("branchActivityTypeId"));
        receipt.setBranchRegionId(rs.getLong("branchRegionId"));
        receipt.setBranchCityId(rs.getLong("branchCityId"));

        receipt.setCompanyId(rs.getLong("companyId"));
        receipt.setCompanyInn(rs.getString("companyInn"));
        receipt.setCompanyName(rs.getString("companyName"));
        receipt.setCompanyRegionId(rs.getLong("companyRegionId"));
        receipt.setCompanyCityId(rs.getLong("companyCityId"));

        receipt.setCreatedById(rs.getLong("createdById"));
        receipt.setCreatedByFirstname(rs.getString("createdByFirstname"));
        receipt.setCreatedByLastname(rs.getString("createdByLastname"));

        receipt.setTerminalId(rs.getLong("terminalId"));
        receipt.setTerminalSerialNumber(rs.getString("terminalSerialNumber"));

        receipt.setCreatedDateTime(rs.getString("createdDateTime"));
        receipt.setReceiptDetailIds(Arrays.stream(((long[]) rs.getArray("receiptDetailIds").getArray())).boxed().collect(Collectors.toList()));
        receipt.setAdditional(rs.getString("additional"));
        return receipt;
    };

}
