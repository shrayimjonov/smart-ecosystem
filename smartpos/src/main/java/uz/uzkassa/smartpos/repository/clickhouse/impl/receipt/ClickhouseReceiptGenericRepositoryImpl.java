package uz.uzkassa.smartpos.repository.clickhouse.impl.receipt;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceipt;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceiptDetail;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CreditAdvanceReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.integration.CheckReturnedReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.CreditAdvanceReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.CreditPaymentDTO;
import uz.uzkassa.smartpos.dto.receipt.LastReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.scheduling.CHCreditDto;
import uz.uzkassa.smartpos.dto.scheduling.SchedulePaymentDto;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.mappers.clickhouse.ClickhouseReceiptDetailRowMappers;
import uz.uzkassa.smartpos.mappers.clickhouse.ClickhouseReceiptMapper;
import uz.uzkassa.smartpos.mappers.clickhouse.ClickhouseReceiptRowMappers;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClickhouseReceiptGenericRepositoryImpl implements ClickhouseReceiptGenericRepository {
    private final JdbcClient jdbcClient;
    private final ClickhouseReceiptMapper clickhouseReceiptMapper;
    @Value("${spring.clickhouse.receipt}")
    private String RECEIPT;
    @Value("${spring.clickhouse.receipt-detail}")
    private String RECEIPT_DETAIL;

    public ClickhouseReceiptGenericRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient, ClickhouseReceiptMapper clickhouseReceiptMapper) {
        this.jdbcClient = jdbcClient;
        this.clickhouseReceiptMapper = clickhouseReceiptMapper;
    }

    @Override
    public Page<ReceiptDTO> findAllByFilter(ReceiptFilter filter, boolean forExcel) {
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        StringBuilder countQuery = new StringBuilder("SELECT count() AS receiptCount");
        StringBuilder selectQuery = new StringBuilder("""
                SELECT id,
                       uid,
                       originUid,
                       receiptDateTime,
                       if(totalCard = 0, payments['CARD'], totalCard) as totalCard,
                       if(totalNds = 0, payments['NDS'], totalNds)    as totalNds,
                       shiftNumber,
                       terminalSerialNumber,
                       if(totalCash = 0, payments['CASH'], totalCash) as totalCash,
                       payments,
                       fiscalSign,
                       fiscalUrl,
                       branchId,
                       branchName,
                       companyName,
                       customerName,
                       customerPhone,
                       status,
                       createdByLastname,
                       createdByFirstname,
                       length(receiptDetailIds)                       AS positionCount,
                       additional,
                       totalCost
                FROM\s""").append(this.RECEIPT).append(" WHERE id IN (SELECT id ");
        var params = new HashMap<String, Object>();
        setFiltersTwo(filter, query, params);

        if (filter.getShiftNumber() != null) {
            query.append(" and shiftNumber = :shiftNumber");
            params.put("shiftNumber", filter.getShiftNumber());
        }
        countQuery.append(query);

        Long total = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();

        if (forExcel) {
            filter.setPage(0);
            filter.setSize(20_000);
        }

        setOrderByField(query, filter);

        ClickhouseUtils.setLimits(query, filter.getPage(), filter.getSize());

        selectQuery.append(query);
        selectQuery.append(")");

        setOrderByField(selectQuery, filter);

        List<ReceiptDTO> receiptList = jdbcClient.sql(selectQuery.toString()).params(params).query(ClickhouseReceiptRowMappers.RECEIPT_DTO).list();
        return new PageImpl<>(receiptList, filter.getPageable(), total);
    }

    @Override
    public Optional<ReceiptDTO> findFirstByOriginUid(String originUid) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + this.RECEIPT + " WHERE originUid = :originUid");
        var params = new HashMap<String, Object>();
        params.put("originUid", originUid);
        return getReceiptDTOHelper(query, false, params);
    }


    @Override
    public List<ReceiptDTO> findAllByOriginUid(String originUid) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + this.RECEIPT + " WHERE originUid = :originUid");
        var params = new HashMap<String, Object>();
        params.put("originUid", originUid);
        return getReceiptListHelper(query, params);
    }

    @Override
    public Optional<ReceiptDTO> findFirstByFiscalUrl(String fiscalUrl) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + this.RECEIPT + " WHERE fiscalUrl = :fiscalUrl");
        var params = new HashMap<String, Object>();
        params.put("fiscalUrl", fiscalUrl);
        return getReceiptDTOHelper(query, false, params);
    }

    @Override
    public Optional<ReceiptDTO> findById(Long id, boolean noFiscal) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + this.RECEIPT + " WHERE id = :id");
        var params = new HashMap<String, Object>();
        params.put("id", id);
        return getReceiptDTOHelper(query, noFiscal, params);
    }

    @Nullable
    private Optional<ReceiptDTO> getReceiptDTOHelper(StringBuilder query, boolean noFiscal, Map<String, Object> params) {
        query.append(" LIMIT 1");
        try {
            Optional<ClickhouseReceipt> optionalClickhouseReceipt = jdbcClient.sql(query.toString()).params(params).query(ClickhouseReceiptRowMappers.CLICKHOUSE_RECEIPT).optional();
            if (optionalClickhouseReceipt.isEmpty()) return Optional.empty();
            query = new StringBuilder("SELECT * FROM " + this.RECEIPT_DETAIL + " WHERE id in (:ids)");
            params.clear();
            params.put("ids", optionalClickhouseReceipt.get().getReceiptDetailIds());
            List<ClickhouseReceiptDetail> receiptDetailList = jdbcClient.sql(query.toString()).params(params).query(ClickhouseReceiptDetailRowMappers.CLICKHOUSE_RECEIPT_DETAIL).list();
            return clickhouseReceiptMapper.toReceiptDTO(optionalClickhouseReceipt, receiptDetailList, noFiscal);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Nullable
    private List<ReceiptDTO> getReceiptListHelper(StringBuilder query, Map<String, Object> params) {
        try {
            List<ClickhouseReceipt> list = jdbcClient.sql(query.toString()).params(params).query(ClickhouseReceiptRowMappers.CLICKHOUSE_RECEIPT).list();
            query = new StringBuilder("SELECT * FROM " + this.RECEIPT_DETAIL + " WHERE id in (:ids)");
            params.clear();
            params.put("ids", list.stream().flatMap(receipt -> receipt.getReceiptDetailIds().stream()).toList());
            List<ClickhouseReceiptDetail> receiptDetailList = jdbcClient.sql(query.toString()).params(params).query(ClickhouseReceiptDetailRowMappers.CLICKHOUSE_RECEIPT_DETAIL).list();
            return clickhouseReceiptMapper.toReceiptListDTO(list, receiptDetailList, true);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public ShiftReportDTO xReport(Long branchId, Long shiftNumber) {
        String query = "select countIf(id, status = 'PAID') as receipts," +
                "       countIf(id, status = 'RETURNED') as receiptsReturned," +
                "       sumIf(payments['CARD'], status='PAID') as totalCard," +
                "       sumIf(payments['CARD'], status = 'RETURNED') as totalCardReturned," +
                "       sumIf(payments['NDS'], status = 'PAID') NDS," +
                "       sumIf(payments['NDS'], status = 'RETURNED') ndsReturned," +
                "       sumIf(payments['CASH'], status='PAID') as totalCash," +
                "       sumIf(payments['CASH'], status = 'RETURNED') as totalCashReturned from " + RECEIPT + " where shiftNumber = :shiftNumber and " +
                "status in (:statuses) and " +
                "branchId = :branchId";
        ShiftReportDTO dto = jdbcClient.sql(query)
                .param("shiftNumber", shiftNumber)
                .param("statuses", List.of(ReceiptStatus.PAID.name(), ReceiptStatus.RETURNED.name()))
                .param("branchId", branchId)
                .query(ShiftReportDTO.class).single();
        dto.setBranchId(branchId);
        return dto;
    }

    @Override
    public ShiftReportDTO totalReport(Long branchId) {
        String query = "select :branchId as branchId," +
                "       countIf(id, status = 'PAID') as receipts," +
                "       countIf(id, status = 'RETURNED') as receiptsReturned," +
                "       sumIf(payments['CARD'], status='PAID') as totalCard," +
                "       sumIf(payments['CARD'], status = 'RETURNED') as totalCardReturned," +
                "       sumIf(payments['NDS'], status = 'PAID') NDS," +
                "       sumIf(payments['NDS'], status = 'RETURNED') ndsReturned," +
                "       sumIf(payments['CASH'], status='PAID') as totalCash," +
                "       sumIf(payments['CASH'], status = 'RETURNED') as totalCashReturned from " + RECEIPT + " where status in (:statuses) and branchId = :branchId";
        return jdbcClient.sql(query)
                .param("statuses", List.of(ReceiptStatus.PAID.name(), ReceiptStatus.RETURNED.name()))
                .param("branchId", branchId)
                .query(ShiftReportDTO.class).single();
    }

    @Override
    public Integer receiptNumberInShift(Long branchId, Long shiftNumber) {
        Integer receiptCount = jdbcClient.sql("select count(id) from " + RECEIPT + " WHERE shiftNumber = :shiftNumber and branchId = :branchId")
                .param("shiftNumber", shiftNumber)
                .param("branchId", branchId)
                .query(Integer.class).single();
        return receiptCount + 1;
    }

    @Override
    public Integer maxShiftNumber(Long branchId) {
        return jdbcClient.sql("select max(shiftNumber) from " + RECEIPT + " WHERE branchId = :branchId")
                .param("branchId", branchId)
                .query(Integer.class).single();
    }

    @Override
    public Page<CreditAdvanceReceiptDTO> findAllCreditAdvanceReceipts(CreditAdvanceReceiptFilter filter) {
        StringBuilder countQuery = new StringBuilder("SELECT count() ");
        StringBuilder selectQuery = new StringBuilder("""
                SELECT id,
                       uid,
                       originUid,
                       customerName,
                       customerPhone,
                       receiptDateTime,
                       status as receiptStatus,
                       totalCost,
                       sum(totalPaid) over (partition by originUid order by receiptDateTime) as totalPaid,
                       count(id) over (partition by originUid order by receiptDateTime)      as paymentCount\s""");
        StringBuilder query = new StringBuilder(" FROM ").append(RECEIPT).append(" WHERE status in (:statuses) and companyId = :companyId");
        var params = new HashMap<String, Object>();
        params.put("statuses", List.of(ReceiptStatus.ADVANCE.name(), ReceiptStatus.CREDIT.name()));
        params.put("companyId", filter.getCompanyId());
        if (StringUtils.isNotBlank(filter.getStatus())) {
            query.append(" and status = :status");
            params.put("status", filter.getStatus());
        }
        if (filter.getBranchId() != null) {
            query.append(" and branchId = :branchId");
            params.put("branchId", filter.getBranchId());
        }
        if (StringUtils.isNotBlank(filter.getOriginUid())) {
            query.append(" and originUid = :originUid");
            params.put("originUid", filter.getOriginUid());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (uid like :search or lowerUTF8(customerName) like :search or lowerUTF8(customerPhone) like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
        selectQuery.append(query);
        countQuery.append(query);
        Integer count = jdbcClient.sql(countQuery.toString()).params(params).query(Integer.class).single();
        selectQuery.append(" ORDER BY receiptDateTime desc ");
        ClickhouseUtils.setLimits(selectQuery, filter.getPageNumber(), filter.getPageSize());
        List<CreditAdvanceReceiptDTO> resultList = jdbcClient.sql(selectQuery.toString()).params(params).query(CreditAdvanceReceiptDTO.class).list();
        return new PageImpl<>(resultList, PageRequest.of(filter.getPageNumber(), filter.getPageSize(), Sort.by("receiptDateTime").descending()), count);
    }

    private void setOrderByField(StringBuilder query, BaseFilter filter) {
        String orderByField = "receiptDateTime";
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            orderByField = switch (filter.getOrderBy()) {
                case "revenue", "totalCost" -> "totalCost";
                case "receipts" -> "receiptCount";
                default -> orderByField;
            };
        }
        query.append(" ORDER BY ").append(orderByField);
        ClickhouseUtils.setSortOrder(query, filter);
    }

    private void setFiltersTwo(ReceiptFilter filter, StringBuilder query, Map<String, Object> params) {
        setFilters(query, filter, params);
        if (CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId()) && filter.getCompanyId() != null) {
            query.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getPaymentTypes() != null) {
            filter.getPaymentTypes().forEach(
                    paymentType -> {
                        query.append(" and ");
                        query.append("payments['").append(paymentType.name()).append("'] > 0 ");
                    }
            );
        }
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                query.append(" and isFiscal = 1");
            } else {
                query.append(" and isFiscal = 0");
            }
        }
        if (filter.getSource() != null) {
            query.append(" and source = :source");
            params.put("source", filter.getSource().name());
        }
        if (filter.getPosition() != null) {
            query.append(" and length(receiptDetailIds) = :position");
            params.put("position", filter.getPosition());
        }

        if (filter.getActivityTypeId() != null) {
            query.append(" and branchActivityTypeId = :activityTypeId");
            params.put("activityTypeId", filter.getActivityTypeId());
        }
        if (CollectionUtils.isNotEmpty(filter.getTerminalVersions())) {
            query.append(" and JSONExtractString(additional, 'terminalVersion') in (:terminalVersions)");
            params.put("terminalVersions", filter.getTerminalVersions());
        }
    }

    private void setDateTimeFilters(StringBuilder query, BaseFilter filter, Map<String, Object> params) {
        if (filter.getFrom() != null) {
            query.append(" and receiptDateTime >= :from");
            params.put("from", filter.getFrom().withNano(0));
        }
        if (filter.getTo() != null) {
            query.append(" and receiptDateTime <= :to");
            params.put("to", filter.getTo().withNano(0));
        }
    }

    private void setFilters(StringBuilder query, ReceiptFilter filter, Map<String, Object> params) {
        setDateTimeFilters(query, filter, params);
        if (filter.getStatus() != null) {
            query.append(" and status = :status");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getStatus() == null && CollectionUtils.isNotEmpty(filter.getStatuses())) {
            query.append(" and status in (:statuses)");
            params.put("statuses", filter.getStatuses().stream().map(Enum::name).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branchId in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (filter.getCompanyId() != null) {
            query.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getCustomerId() != null) {
            query.append(" and customerId = :customerId");
            params.put("customerId", filter.getCustomerId());
        }
        if (filter.getUserId() != null) {
            query.append(" and createdById = :userId");
            params.put("userId", filter.getUserId());
        }
        if (filter.getTerminalId() != null) {
            query.append(" and terminalId = :terminalId");
            params.put("terminalId", filter.getTerminalId());
        }
        if (filter.getTotalCostFrom() != null) {
            query.append(" and totalCost >= :totalCostFrom");
            params.put("totalCostFrom", filter.getTotalCostFrom());
        }
        if (filter.getTotalCostTo() != null) {
            query.append(" and totalCost <= :totalCostTo");
            params.put("totalCostTo", filter.getTotalCostTo());
        }
        if (CollectionUtils.isNotEmpty(filter.getUidList())) {
            query.append(" and uid in (:uids)");
            params.put("uids", filter.getUidList());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (uid like :search or originUid like :search or fiscalUrl like :search or lowerUTF8(customerName) like :search or lowerUTF8(customerPhone) like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
    }

    @Override
    public List<CreditPaymentDTO> findAllCreditPayments(String originUid) {
        return jdbcClient.sql("SELECT totalPaid, " +
                        "if(totalDiscount = 0, payments['DISCOUNT'], totalDiscount) as totalDiscount, " +
                        "receiptDateTime, status, if(totalNds = 0, payments['NDS'], totalNds) as totalNds, " +
                        "payments FROM " + RECEIPT + " WHERE status IN ('CREDIT', 'ADVANCE') AND originUid = :originUid order by receiptDateTime")
                .param("originUid", originUid)
                .query((rs, rowNum) ->
                        new CreditPaymentDTO(
                                rs.getBigDecimal("totalPaid"),
                                rs.getBigDecimal("totalNds"),
                                rs.getBigDecimal("totalDiscount"),
                                ReceiptStatus.valueOf(rs.getString("status")),
                                rs.getObject("receiptDateTime", LocalDateTime.class),
                                ((Map<String, BigDecimal>) rs.getObject("payments", Map.class)).entrySet().stream()
                                        .collect(Collectors.toMap(e -> PaymentType.valueOf(e.getKey()), Map.Entry::getValue))
                        )
                ).list();
    }

    @Override
    public List<SchedulePaymentDto> findAllCreditPayments(Long id) {
        return jdbcClient.sql("SELECT totalPaid as paid, " +
                        "if(totalNds = 0, payments['NDS'], totalNds) as vat, " +
                        "receiptDateTime as paidDateTime " +
                        " FROM " + RECEIPT + " WHERE id = :id")
                .param("id", id)
                .query(SchedulePaymentDto.class).list();
    }

    @Override
    public Integer getAdvanceOrderFromOriginUid(String originUid) {
        return jdbcClient.sql("select uniq(id) from " + RECEIPT + " where originUid = :originUid and status = 'ADVANCE'")
                .param("originUid", originUid)
                .query(Integer.class).single();
    }

    @Override
    public Optional<Pair<ReceiptStatus, String>> findUidByFiscalUrl(String fiscalUrl) {
        return jdbcClient.sql("SELECT uid, status FROM " + RECEIPT + " WHERE fiscalUrl = :fiscalUrl limit 1")
                .param("fiscalUrl", fiscalUrl)
                .query((rs, rowNum) -> Pair.of(ReceiptStatus.valueOf(rs.getString("status")), rs.getString("uid"))).optional();
    }

    @Override
    public Optional<CHCreditDto> findAdditionalByOriginUid(String originUid) {
        return jdbcClient.sql("select id," +
                        "       branchId," +
                        "       JSONExtractBool(additional, 'forCredit') forCredit," +
                        "       JSONExtractInt(additional, 'month')      month" +
                        " from " + RECEIPT +
                        " where originUid = :originUid order by id limit 1")
                .param("originUid", originUid)
                .query(CHCreditDto.class).optional();
    }

    @Override
    public Optional<CheckReturnedReceiptDto> checkReceiptByOriginUid(String uid, List<ReceiptStatus> statuses) {
        return jdbcClient.sql("select uid, status from " + RECEIPT + " where originUid = :uid and status in (:statuses) limit 1")
                .param("uid", uid)
                .param("statuses", statuses.stream().map(Enum::name).collect(Collectors.toList()))
                .query((rs, rowNum) -> new CheckReturnedReceiptDto(rs.getString("uid"), ReceiptStatus.valueOf(rs.getString("status")))).optional();
    }

    @Override
    public Optional<LastReceiptDto> getLastReceipt(String serialNumber, String tin, PaymentType paymentType) {
        return jdbcClient.sql("select receiptDateTime , fiscalUrl  from "
                        + RECEIPT
                        + " where companyInn=:companyInn and terminalSerialNumber = :serialNumber and payments[:paymentType] > 0 order by receiptDateTime desc limit 1 ")
                .param("serialNumber", serialNumber)
                .param("companyInn", tin)
                .param("paymentType", paymentType.name())
                .query(LastReceiptDto.class)
                .optional();
    }
}
