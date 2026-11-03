package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.warehouse.WarehouseDTO;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;

/**
 * A Warehouse.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "warehouse",
indexes = {
    @Index(columnList = "organization_id", name = "warehouse_organization_id_idx"),
    @Index(columnList = "operation", name = "warehouse_operation_idx"),
    @Index(columnList = "unit", name = "warehouse_unit_idx")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE warehouse SET deleted = 'true' WHERE id=?")
public class Warehouse extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id")
    private String productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id")
    private String serialId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private WarehouseOperation operation;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;

    @Column(name = "amount_start")
    private BigDecimal amountStart;

    private BigDecimal amount = BigDecimal.ZERO;
    @Column(name = "amount_end")
    private BigDecimal amountEnd;

//    @Column(name = "to_organization_id")
//    private String toOrganizationId;
//    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
//    @JoinColumn(name = "to_organization_id", updatable = false, insertable = false)
//    private Organization toOrganization;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String note;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private PackageType unit;

    @Column(name = "code")
    private String code;

    @Column(name = "from_shipment")
    private Boolean fromShipment;

    public WarehouseDTO toDto() {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setProductId(getProductId());
        dto.setProductName(getProduct().getVisibleName());
        dto.setOperation(getOperation().name());
        dto.setOperationName(getOperation().getNameRu());
        dto.setOperationDate(getOperationDate());
        dto.setAmount(getAmount());
        dto.setUnit(getUnit());
        dto.setUnitName(getUnit().getNameRu());
        return dto;
    }
}
