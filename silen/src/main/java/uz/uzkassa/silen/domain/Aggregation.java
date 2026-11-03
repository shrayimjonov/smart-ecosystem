package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.marking.AggregationDTO;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.service.ConvertableToAggregationItem;
import uz.uzkassa.silen.service.MultiUnitCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A Aggregation.
 */
@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "aggregation",
        indexes = {
                @Index(columnList = "organization_id,status", name = "aggregation_org_sta_idx")
        })
@SQLDelete(sql = "UPDATE aggregation SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class Aggregation extends BaseOrganizationEntity implements ConvertableToAggregationItem, MultiUnitCode, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    private Integer quantity;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "aggregation_date")
    private LocalDateTime aggregationDate;

    @Column(name = "print_code", nullable = false, unique = true)
    private String printCode;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "sscc_code", nullable = false)
    private String ssccCode;

    @Column(columnDefinition = "int4 default 0", nullable = false)
    private int unit;

    @Column(name = "company_prefix", nullable = false)
    private String companyPrefix;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "report_id")
    private String reportId;

    @Enumerated(EnumType.STRING)
    private AggregationStatus status = AggregationStatus.CREATED;

    @Column(name = "aggregation_type", columnDefinition = "varchar(255) default 'AGGREGATION'")
    @Enumerated(EnumType.STRING)
    private AggregationType aggregationType = AggregationType.AGGREGATION;

    @Column(name = "shipment_id")
    private String shipmentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    private Shipment shipment;

    @Column(name = "batch_id")
    private String batchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", updatable = false, insertable = false)
    private BatchAggregation batch;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNum;

    @Column(name = "login")
    private String login;

    @Column(name = "gcp")
    private String gcp;

    @Column(name = "utilisation_id")
    private String utilisationId;

    @Column(name = "utilisation_time")
    private LocalDateTime utilisationTime;

    @Column(name = "production_date")
    private LocalDateTime productionDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "partition_id")
    private String partitionId;

    @Column(name = "party_aggregation_id")
    private String partyAggregationId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "party_aggregation_id", updatable = false, insertable = false)
    private PartyAggregation partyAggregation;

    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;

    @Column(name = "checked", columnDefinition = "int4 default 0", nullable = false)
    int checked;

    @Override
    public Integer getCapacity() {
        return getQuantity();
    }

    public AggregationStatus getStatus() {
        return status == null ? AggregationStatus.CREATED : status;
    }

    public AggregationType getAggregationType() {
        return aggregationType == null ? AggregationType.AGGREGATION : this.aggregationType;
    }

    public PackageType getPackageType() {
        return PackageType.fromCode(getUnit());
    }

    public AggregationDTO toDto(boolean forAdmin) {
        AggregationDTO aggregationDTO = new AggregationDTO();
        aggregationDTO.setId(getId());
        aggregationDTO.setQuantity(getQuantity());
        aggregationDTO.setPrintCode(getPrintCode());
        aggregationDTO.setCode(getCode());
        aggregationDTO.setDescription(getDescription());
        aggregationDTO.setSerialId(getSerialId());
        aggregationDTO.setGcp(getGcp());
        aggregationDTO.setTuronAggregationId(getReportId());
        PackageType unit = PackageType.fromCode(getUnit());
        aggregationDTO.setUnit(unit);
        aggregationDTO.setUnitName(unit.getNameRu());
        aggregationDTO.setAggregationDate(getAggregationDate());
        aggregationDTO.setUtilisationId(getUtilisationId());
        aggregationDTO.setUtilisationTime(getUtilisationTime());
        aggregationDTO.setProductId(getProductId());
        aggregationDTO.setProductionDate(getProductionDate());
        aggregationDTO.setExpirationDate(getExpirationDate());
        aggregationDTO.setErrorMessage(getErrorMessage());
        aggregationDTO.setChecked(getChecked());
        if (getStatus() != null) {
            aggregationDTO.setStatus(getStatus());
            aggregationDTO.setStatusName(getStatus().getText());
        }
        if (forAdmin && getOrganization() != null) {
            aggregationDTO.setOrganizationName(getOrganization().getName());
            aggregationDTO.setOrganizationId(getOrganizationId());
        }
        if (getProduct() != null) {
            aggregationDTO.setProductName(getProduct().getVisibleName());
            aggregationDTO.setBarcode(getProduct().getBarcode());
            aggregationDTO.setParentBarcode(getProduct().getParentBarcode());
        }
        if (getShipment() != null) {
            aggregationDTO.setShipmentId(getShipmentId());
            aggregationDTO.setShipmentNumber(getShipment().getNumber());
        }

        if (getSerialNum() != null) {
            aggregationDTO.setSerial(getSerialNum().toSelectItem());
        }
        if (getAggregationType() != null) {
            aggregationDTO.setType(getAggregationType());
            aggregationDTO.setTypeName(getAggregationType().getText());
        }
        if (getPartyAggregation() != null) {
            aggregationDTO.setPartyAggregation(getPartyAggregation().toSelectItem());
        }

        return aggregationDTO;
    }

    public AggregationStatusChangedPayload toPayload() {
        AggregationStatusChangedPayload payload = new AggregationStatusChangedPayload();
        payload.setPartyAggregationId(getPartyAggregationId());
        payload.setStatus(getStatus().name());
        payload.setCode(getCode());
        payload.setStatusName(getStatus().getText());
        payload.setId(getId());
        return payload;
    }
}
