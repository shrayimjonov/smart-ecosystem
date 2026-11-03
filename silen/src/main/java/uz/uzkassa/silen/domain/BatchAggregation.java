package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;
import uz.uzkassa.silen.enumeration.BatchAggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serial;
import java.io.Serializable;

/**
 * A BatchAggregation.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "batch_aggregation",
    indexes = {
        @Index(columnList = "created_date", name = "batch_aggregation_created_date_idx"),
        @Index(columnList = "description", name = "batch_aggregation_description_idx"),
        @Index(columnList = "organization_id", name = "batch_aggregation_organization_id_idx"),
    })
@SQLDelete(sql = "UPDATE batch_aggregation SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BatchAggregation extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(columnDefinition = "int4 default 0", nullable = false)
    private int unit;

    @Column(name = "gcp", length = 15)
    private String gcp;

    private Integer quantity;

    private String description;

    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;

    @Enumerated(EnumType.STRING)
    private BatchAggregationStatus status;

    @Column(name = "export_to_file", columnDefinition = "boolean default false")
    private Boolean exportToFile;

    public BatchAggregationListDTO toDto() {
        BatchAggregationListDTO dto = setAuditValues(new BatchAggregationListDTO());
        dto.setId(getId());
        dto.setNumber(getNumber());
        PackageType unit = PackageType.fromCode(getUnit());
        dto.setUnit(unit);
        dto.setUnitName(unit.getNameRu());
        dto.setQuantity(getQuantity());
        dto.setDescription(getDescription());
        dto.setGcp(getGcp());
        dto.setErrorMessage(getErrorMessage());
        dto.setErrorMessage(getErrorMessage());
        if (getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(getStatus().getRuText());
        }
        return dto;
    }
}
