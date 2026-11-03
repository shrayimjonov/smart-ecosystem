package uz.uzkassa.smartpos.dto.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesDetailClickhouseDto {
    private Long id;
    private Long version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
    private Long productId;
    private String productName;
    private String vatBarcode;
    private String productBarcode;
    private String searchField;
    private Long unitId;
    private String unitName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal qty;
    private BigDecimal costAmount;
    private BigDecimal amount;
    private BigDecimal vat;
    private BigDecimal vatPercent;
    private BigDecimal discount;
    private BigDecimal discountPercent;
    private Set<String> marks;
    private Long salesId;
    private Long createdById;
    private String status;
    private String type;
    private Long companyId;
    private Long branchId;
    private String branchName;
    private String info;
    private Long promotionId;

    public SalesDetailClickhouseDto(SalesDetail salesDetail, BigDecimal costPrice, ObjectMapper objectMapper) {
        this.id = salesDetail.getId();
        this.version = salesDetail.getVersion();
        this.productId = salesDetail.getProductId();
        this.productName = salesDetail.getProductName();
        this.vatBarcode = salesDetail.getVatBarcode();
        this.productBarcode = salesDetail.getProductBarcode();
        this.searchField = Stream.of(salesDetail.getProductName(), salesDetail.getProductBarcode(), salesDetail.getVatBarcode())
                .filter(StringUtils::isNotBlank).map(String::toLowerCase).collect(Collectors.joining("///"));
        this.unitId = salesDetail.getUnitId();
        this.unitName = salesDetail.getUnitName();
        this.categoryId = salesDetail.getCategoryId();
        this.categoryName = salesDetail.getCategoryName();
        this.qty = salesDetail.getQty();
        this.costAmount = Optional.ofNullable(costPrice).map(cost -> cost.multiply(salesDetail.getQty())).orElse(salesDetail.getAmount());
        this.amount = salesDetail.getAmount();
        this.vat = salesDetail.getVat();
        this.vatPercent = salesDetail.getVatPercent();
        this.discount = salesDetail.getDiscount();
        this.discountPercent = salesDetail.getDiscountPercent();
        if (CollectionUtils.isNotEmpty(salesDetail.getMarks())) {
            this.marks = salesDetail.getMarks();
        }
        this.promotionId = salesDetail.getPromotionId();
        this.createdDateTime = salesDetail.getCreatedDateTime();
        if (salesDetail.getSales() != null) {
            this.salesId = salesDetail.getSales().getId();
            this.createdById = salesDetail.getSales().getCreatedById();
            this.status = salesDetail.getSales().getStatus().name();
            this.type = salesDetail.getSales().getType().name();
            this.companyId = salesDetail.getSales().getCompanyId();
            this.branchId = salesDetail.getSales().getBranchId();
            if (salesDetail.getSales().getBranch() != null) {
                this.branchName = salesDetail.getSales().getBranch().getName();
            }
        }
        try {
            this.info = objectMapper.writeValueAsString(new SalesDetailClickhouseInfoDto(salesDetail));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
