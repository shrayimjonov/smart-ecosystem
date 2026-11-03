package uz.uzkassa.smartpos.dto.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.sales.Sales}
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesClickhouseDto implements Serializable {
    private Long id;
    private Integer sign = 1;
    private Long version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
    private String uid;
    private String originUid;
    private String fiscalUrl;
    private Long shiftNumber;
    private BigDecimal costAmount;
    private BigDecimal totalCost;
    private String type;
    private String status;
    private String source;
    private Map<PaymentType, BigDecimal> payments;
    private Long branchId;
    private String branchName;
    private Long branchActivityId;
    private Long branchRegionId;
    private Long branchCityId;
    private Long companyId;
    private String companyName;
    private String companyTin;
    private String customer;
    private Long createdById;
    private String createdByName;
    private String terminalSN;
    private String facturaId;
    private String searchField/*uid, fiscalUrl*/;
    private List<Long> salesDetailIds;
    private String info;
    private Long promotionId;


    public SalesClickhouseDto(Sales sales, BigDecimal costAmount, ObjectMapper objectMapper) {
        this.id = sales.getId();
        this.sign = 1;
        this.version = sales.getVersion();
        this.createdDateTime = sales.getCreatedDateTime();
        this.uid = sales.getUid();
        this.originUid = sales.getOriginUid();
        this.fiscalUrl = sales.getFiscalUrl();
        this.shiftNumber = sales.getShiftNumber();
        this.costAmount = costAmount != null && costAmount.compareTo(BigDecimal.ZERO) > 0 ? costAmount : sales.getTotalCost();
        this.totalCost = sales.getTotalCost();
        this.type = sales.getType().name();
        this.status = sales.getStatus().name();
        this.source = sales.getSource().name();
        this.payments = sales.getPayments();
        this.branchId = sales.getBranchId();
        if (sales.getBranch() != null) {
            this.branchName = sales.getBranch().getName();
            this.branchActivityId = sales.getBranch().getActivityTypeId();
            this.branchRegionId = sales.getBranch().getRegionId();
            this.branchCityId = sales.getBranch().getCityId();
        }
        this.companyId = sales.getCompanyId();
        if (sales.getCompany() != null) {
            this.companyName = sales.getCompany().getName();
            this.companyTin = sales.getCompany().getInn();
        }
        if (sales.getAdditional() != null) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            Optional.ofNullable(sales.getAdditional().getCustomerName())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(name -> jsonNode.put("customerName", name));

            Optional.ofNullable(sales.getAdditional().getCustomerPhone())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(phone -> jsonNode.put("customerPhone", phone));

            Optional.ofNullable(sales.getAdditional().getCustomerId())
                    .ifPresent(phone -> jsonNode.put("customerId", phone));

            if (sales.getAdditional().isNewCustomer()) {
                jsonNode.put("isNewCustomer", true);
                jsonNode.put("customerCreatedDate", String.valueOf(LocalDateTime.now()));
            }
            this.customer = jsonNode.isEmpty() ? null : jsonNode.toString();
        }
        this.createdById = sales.getCreatedById();
        if (sales.getCreatedBy() != null) {
            this.createdByName = sales.getCreatedBy().getFullNameStr();
        }
        this.terminalSN = sales.getTerminalSN();
        this.facturaId = sales.getFacturaId();
        this.searchField = Stream.of(sales.getUid(), sales.getFiscalUrl())
                .filter(StringUtils::isNotBlank)
                .map(String::toLowerCase)
                .collect(Collectors.joining("///"));
        if (CollectionUtils.isNotEmpty(sales.getSalesDetails())) {
            this.salesDetailIds = sales.getSalesDetails().stream().map(SalesDetail::getId).collect(Collectors.toList());
        }
        try {
            this.info = objectMapper.writeValueAsString(new SalesClickhouseInfoDto(sales));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.promotionId = sales.getPromotionId();
    }
}