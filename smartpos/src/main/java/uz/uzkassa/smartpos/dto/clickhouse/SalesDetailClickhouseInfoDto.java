package uz.uzkassa.smartpos.dto.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesDetailClickhouseInfoDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productBarcode;
    private String vatBarcode;
    private CommonDTO unit;
    private CommonDTO category;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal amount;
    private BigDecimal vat;
    private BigDecimal vatPercent;
    private BigDecimal discount;
    private BigDecimal discountPercent;
    private BigDecimal exciseRate;
    private BigDecimal excise;
    private Set<String> marks;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiptDateTime;
    private Long salesId;
    private Long createdById;
    private String status;
    private String type;
    private CompanyBaseDTO company;
    private CommonDTO branch;
    private String committentTin;
    private ExtraDetail extraDetail;

    public SalesDetailClickhouseInfoDto(SalesDetail salesDetail) {
        this.id = salesDetail.getId();
        this.productId = salesDetail.getProductId();
        this.productName = salesDetail.getProductName();
        this.productBarcode = salesDetail.getProductBarcode();
        this.vatBarcode = salesDetail.getVatBarcode();
        this.unit = new CommonDTO(salesDetail.getUnitId(), salesDetail.getUnitName());
        this.category = new CommonDTO(salesDetail.getCategoryId(), salesDetail.getCategoryName());
        this.price = salesDetail.getPrice();
        this.qty = salesDetail.getQty();
        this.amount = salesDetail.getAmount();
        this.vat = salesDetail.getVat();
        this.vatPercent = salesDetail.getVatPercent();
        this.discount = salesDetail.getDiscount();
        this.discountPercent = salesDetail.getDiscountPercent();
        this.exciseRate = salesDetail.getExciseRate();
        this.excise = salesDetail.getExcise();
        this.marks = salesDetail.getMarks();
        this.createdDateTime = salesDetail.getCreatedDateTime();
        if (salesDetail.getSales() != null) {
            this.receiptDateTime = salesDetail.getSales().getReceiptDateTime();
            this.salesId = salesDetail.getSales().getId();
            this.createdById = salesDetail.getSales().getCreatedById();
            this.status = salesDetail.getSales().getStatus().name();
            this.type = salesDetail.getSales().getType().name();
            if (salesDetail.getSales().getCompany() != null) {
                this.company = salesDetail.getSales().getCompany().toCompanyBaseDTO();
            }
            if (salesDetail.getSales().getBranch() != null) {
                this.branch = salesDetail.getSales().getBranch().toCommonDTO();
            }
        }
        this.committentTin = salesDetail.getCommittentTin();
        this.extraDetail = salesDetail.getExtraDetail();
    }
}
