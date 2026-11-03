package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.StoreInItemDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;

import java.util.LinkedHashSet;

@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "store_in_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInItem extends BaseIdentityEntity {

    @Column(name = "store_in_id")
    String storeInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_in_id", updatable = false, insertable = false)
    StoreIn storeIn;

    @NotNull
    @Column(name = "product_id", nullable = false)
    String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @Column(columnDefinition = "int default 0")
    private int qty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private LinkedHashSet<ShipmentCode> types = new LinkedHashSet<>();

    public StoreInItemDTO toDto() {
        StoreInItemDTO dto = new StoreInItemDTO();
        dto.setId(getId());
        dto.setProductId(getProductId());
        dto.setStoreInId(getStoreInId());
        dto.setQty(getQty());
        if (getProduct() != null) {
            dto.setProduct(getProduct().toCommonDTO());
        }
        if (getSerialNumber() != null) {
            dto.setSerialNumber(getSerialNumber().toSelectItem());
        }
        return dto;
    }
}
