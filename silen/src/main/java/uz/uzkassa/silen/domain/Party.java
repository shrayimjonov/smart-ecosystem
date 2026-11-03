package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.marking.PartyDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.CisType;
import uz.uzkassa.silen.enumeration.PartyStatus;
import uz.uzkassa.silen.enumeration.UsageType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "party",
        indexes = {
                @Index(columnList = "last_modified_date", name = "party_last_modified_date_idx"),
                @Index(columnList = "order_product_id", name = "party_order_product_id_idx"),
                @Index(columnList = "organization_id", name = "party_organization_id_idx"),
        })
@SQLDelete(sql = "UPDATE party SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Party extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 872342341L;

    @NotNull
    @Column(name = "order_product_id", nullable = false)
    private String orderProductId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id", updatable = false, insertable = false)
    private OrderProduct orderProduct;

    private Integer quantity;

    @Column(name = "block_id")
    private String blockId;

    @Column(name = "status", length = 100)
    @Enumerated(EnumType.STRING)
    private PartyStatus status = PartyStatus.EMITTED;

    @Column(name = "usage_type", length = 100)
    @Enumerated(EnumType.STRING)
    private UsageType usageType = UsageType.USED_FOR_PRODUCTION;

    @Column(name = "report_id")
    String reportId;

    @Column(name = "report_time")
    LocalDateTime reportTime;

    @Column(name = "description", columnDefinition = "varchar(1024)")
    private String description;

    @Column(name = "job_status", length = 100)
    @Enumerated(EnumType.STRING)
    private AggregationStatus jobStatus;
    @Column(name = "error_message")
    private String errorMessage;

    public PartyDTO toDto() {
        PartyDTO dto = setAuditValues(new PartyDTO());
        dto.setOrderProductId(getOrderProductId());
        if (getOrderProduct() != null) {
            dto.setProductId(getOrderProduct().getProductId());
            dto.setProductName(getOrderProduct().getProduct().getName());
            dto.setRateType(getOrderProduct().getRateType());
            dto.setSerialNumberType(getOrderProduct().getSerialNumberType());
            dto.setCisType(Optional.ofNullable(getOrderProduct().getCisType()).orElse(CisType.UNIT));
            dto.setCisTypeName(dto.getCisType().getNameRu());
        }
        dto.setQuantity(getQuantity());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getText());
        }
        dto.setReportId(getReportId());
        dto.setReportTime(getReportTime());
        dto.setUsageType(getUsageType());
        dto.setUsageType(getUsageType());
        dto.setTuronPartyId(getBlockId());
        dto.setDescription(getDescription());
        dto.setJobStatus(getJobStatus());
        if (getJobStatus() != null) {
            dto.setJobStatusName(getJobStatus().getText());
        }
        dto.setErrorMessage(getErrorMessage());
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getOrderProduct().getProduct().getName(), this.getDescription(), BigDecimal.valueOf(this.getQuantity()));
    }
}
