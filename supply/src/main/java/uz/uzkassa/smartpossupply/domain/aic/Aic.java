package uz.uzkassa.smartpossupply.domain.aic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
@Table(name = "aic", indexes = {
        @Index(columnList = "company_id", name = "aic_company_id_idx"),
        @Index(columnList = "product_id", name = "aic_product_id_idx"),
})
@SQLDelete(sql = "UPDATE aic SET deleted = true WHERE id=?")
public class Aic extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 85938455333L;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    AicStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "aic_codes", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @MapKeyColumn(name = "code")
    @Column(name = "code_type", nullable = false)
    @Enumerated(EnumType.STRING)
    Map<String, CodeType> codes = new HashMap<>();

    @Column(name = "report_id")
    String reportId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "report_content")
    String reportContent;

    @Column(name = "report_time")
    LocalDateTime reportTime;

    @Column(name = "document_id")
    String documentId;

    @Column(name = "qty")
    int qty;

    @Column(name = "mark_qty", precision = 25)
    BigDecimal markQty;

    @Column(name = "tnved_add_units", precision = 25, scale = 2)
    BigDecimal tnvedAddUnits;

    @Column(name = "tnved_add_units_count", precision = 25, scale = 2)
    BigDecimal tnvedAddUnitsCount;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Override
    public CommonDTO toCommonDTO() {
        return new CommonDTO(id, reportId);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AIC;
    }

    @Override
    public String getEntityName() {
        return getDocumentId();
    }

    public void setStatus(AicStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setReportId(String reportId) {
        addLogHistory("reportId", this.reportId, reportId);
        this.reportId = reportId;
    }

    public void setReportTime(LocalDateTime reportTime) {
        addLogHistory("reportTime", this.reportTime, reportTime);
        this.reportTime = reportTime;
    }

    public void setDocumentId(String documentId) {
        addLogHistory("documentId", this.documentId, documentId);
        this.documentId = documentId;
    }
}
