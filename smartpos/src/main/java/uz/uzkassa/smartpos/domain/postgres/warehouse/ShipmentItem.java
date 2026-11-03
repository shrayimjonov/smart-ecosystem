package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.enums.ProductOrigin;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * An Order.
 */
@Getter
@Setter
@Entity
@Table(name = "shipment_item")
public class ShipmentItem extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @NotNull
    @Column(name = "city_id", nullable = false)
    private Long cityId;
    @NotNull
    @Column(name = "unit_id", nullable = false)
    private Long unitId;
    @ManyToOne
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private Unit unit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
            name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
        @JoinColumn(
            name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
        @JoinColumn(
            name = "city_id", referencedColumnName = "city_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    })
    private ProductBranch product;
    @NotBlank
    private String vatBarcode;
    private String barcode;
    @NotBlank
    private String name;
    @NotBlank
    private String packageCode;
    @NotBlank
    private String packageName;
    @NotNull
    @Column(precision = 25, scale = 4)
    private BigDecimal price;
    @Column(precision = 25, scale = 4)
    private BigDecimal excise;
    @Column(precision = 25, scale = 2)
    private BigDecimal vatRate;
    @Enumerated(EnumType.STRING)
    private ProductOrigin origin = ProductOrigin.IMPORT;
    @ElementCollection
    @CollectionTable(name = "shipment_marking", joinColumns = {@JoinColumn(name = "shipment_item_id")})
    @Column(name = "marking")
    private Set<String> markings = new HashSet<>();
    @ElementCollection
    @CollectionTable(name = "shipment_aggregation", joinColumns = {@JoinColumn(name = "shipment_item_id")})
    @Column(name = "aggregation")
    private Set<String> aggregations = new HashSet<>();

    @Column(precision = 25, scale = 2)
    private BigDecimal qty = BigDecimal.ZERO;

    public BigDecimal getQty() {
        if (this.qty == null) {
            this.qty = BigDecimal.ZERO;
        }
        return qty;
    }
}
