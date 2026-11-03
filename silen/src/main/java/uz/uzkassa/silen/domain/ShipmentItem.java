package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "shipment_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ShipmentItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "shipment_id", nullable = false)
    private String shipmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    private Shipment shipment;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id"/*, nullable = false*/)
    private String serialId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private LinkedHashSet<ShipmentCode> types = new LinkedHashSet<>();

    @Column(precision = 25, scale = 2)
    private BigDecimal qty;

    @Column(precision = 25, scale = 2)
    private BigDecimal orderQty;

    @ColumnDefault("0.00")
    @Column(name = "profit_rate", precision = 25, scale = 2)
    private BigDecimal profitRate = BigDecimal.ZERO;

    @Column(name = "return_id")
    Long returnId;

    public BigDecimal getQty() {
        if (this.qty == null) {
            this.qty = BigDecimal.ZERO;
        }
        return qty;
    }

    public ShipmentItemDTO toDto(boolean forList) {
        ShipmentItemDTO dto = new ShipmentItemDTO();
        dto.setId(getId());
        dto.setShipmentId(getShipmentId());
        if (!forList && getShipment() != null) {
            dto.setShipmentNumber(getShipment().getNumber());
            dto.setShipmentStatus(getShipment().getStatus());
            if (getShipment().getStatus() != null) {
                dto.setShipmentStatusName(getShipment().getStatus().getText());
            }
        }
        dto.setProductId(getProductId());
        if (getProduct() != null) {
            dto.setProductName(getProduct().getVisibleName());
            dto.setBarcode(getProduct().getBarcode());
            if (getProduct().getPackageType() != null) {
                dto.setPackageType(getProduct().getPackageType());
            }
        }
        dto.setTypes(getTypes());
        dto.setQty(getQty());
        dto.setOrderQty(getOrderQty());
        dto.setSerialId(getSerialId());
        if (getSerialNumber() != null) {
            dto.setSerialNumber(getSerialNumber().getNameWithDate());
        }

        return dto;
    }
}
