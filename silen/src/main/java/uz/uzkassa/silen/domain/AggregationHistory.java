package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.AggregationHistoryDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serial;
import java.io.Serializable;

@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "aggregation_history",
    indexes = {
        @Index(columnList = "organization_id,status", name = "agregation_history_org_sta_idx")
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AggregationHistory extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 12341245L;


    @Column(name = "aggregation_id")
    private String aggregationId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AggregationStatus status;

    @Column(name = "print_code")
    private String printCode;

    @Column(name = "code")
    private String code;

    @Column(name = "login")
    private String login;

    private Integer quantity;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "block_id")
    private String blockId;

    @Column(name = "turon_aggregation_id")
    private String turonAggregationId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Column(columnDefinition = "int4 default 0", nullable = false)
    private int unit;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SerialNumber serialNumber;

    @Column(name = "shipment_id")
    private String shipmentId;

    @Column(name = "shipment_number")
    private String shipmentNumber;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shipment shipment;

    public AggregationHistoryDTO toDto(boolean isAdmin) {
        AggregationHistoryDTO dto = new AggregationHistoryDTO();
        dto.setId(getId());
        dto.setAggregationId(getAggregationId());
        dto.setUnit(getUnit());
        dto.setUnitName(PackageType.fromCode(getUnit()).getNameRu());
        if (getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(getStatus().getText());
        }
        dto.setPrintCode(getPrintCode());
        dto.setLogin(getLogin());
        dto.setQuantity(getQuantity());
        dto.setCreatedDate(getCreatedDate());
        dto.setTuronAggregationId(getTuronAggregationId());
        if (getProduct() != null) {
            dto.setProductName(getProduct().getVisibleName());
        }
        if (isAdmin && getOrganization() != null) {
            dto.setOrganizationId(getOrganizationId());
            dto.setOrganizationName(getOrganization().getName());
        }
        if (getShipment() != null) {
            dto.setShipmentId(getShipmentId());
            dto.setShipmentNumber(getShipment().getNumber());
        } else {
            dto.setShipmentId(getShipmentId());
            dto.setShipmentNumber(getShipmentNumber());
        }
        dto.setSerialId(getSerialId());
        return dto;
    }

    public static AggregationHistory fromAggregation(Aggregation aggregation) {
        AggregationHistory history = new AggregationHistory();
        history.setStatus(aggregation.getStatus());
        history.setUnit(aggregation.getUnit());
        history.setProductId(aggregation.getProductId());
        history.setQuantity(aggregation.getQuantity());
        history.setLogin(aggregation.getLogin());
        history.setPrintCode(aggregation.getPrintCode());
        history.setCode(aggregation.getCode());
        history.setTuronAggregationId(aggregation.getReportId());
        history.setOrganizationId(aggregation.getOrganizationId());
        history.setSerialId(aggregation.getSerialId());
        history.setShipmentId(aggregation.getShipmentId());
        history.setAggregationId(aggregation.getId());
        return history;
    }

    public static AggregationHistory fromBlock(Block aggregation) {
        AggregationHistory history = new AggregationHistory();
        history.setStatus(aggregation.getStatus());
        history.setUnit(PackageType.BLOCK.getCode());
        history.setProductId(aggregation.getProductId());
        history.setQuantity(aggregation.getQuantity());
        history.setLogin(aggregation.getLogin());
        history.setPrintCode(aggregation.getPrintCode());
        history.setCode(aggregation.getCode());
        history.setOrganizationId(aggregation.getOrganizationId());
//        history.setTuronAggregationId(aggregation.getReportId());
//        history.setSerialId(aggregation.getSerialId());
//        history.setShipmentId(aggregation.getShipmentId());
//        history.setShipmentNumber(aggregation.getShipmentNumber());
//        history.setAggregationId(aggregation.getId());
        history.setBlockId(aggregation.getId());
        return history;
    }
}
