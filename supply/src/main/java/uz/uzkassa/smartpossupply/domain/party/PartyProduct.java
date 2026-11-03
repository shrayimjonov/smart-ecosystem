package uz.uzkassa.smartpossupply.domain.party;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.dto.party.PartyProductListDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;


@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "PartyProductMapper",
                        classes = @ConstructorResult(
                                targetClass = PartyProductListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "productId", type = Long.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "productBarcode", type = String.class),
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "aggregations", type = Long.class),
                                        @ColumnResult(name = "marks", type = Long.class),
                                        @ColumnResult(name = "packageType", type = String.class),
                                        @ColumnResult(name = "aic", type = String.class),
                                        @ColumnResult(name = "aicId", type = Long.class)

                                }
                        )
                ),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "party_product", indexes = {
        @Index(columnList = "product_id", name = "party_product_product_id_idx"),
        @Index(columnList = "party_id", name = "party_product_party_id_idx")
})
@SQLDelete(sql = "UPDATE party_product SET deleted = true WHERE id=?")
public class PartyProduct extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9094725456L;

    @Column(name = "product_id")
    Long productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "child_product_id")
    Long childProductId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_product_id", updatable = false, insertable = false)
    Product childProduct;


    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    ProductType productType;

    @Column(name = "package_type")
    @Enumerated(EnumType.STRING)
    PackageType packageType;

    @Column(name = "qty_in_package")
    int qtyInPackage;

    @Column(name = "aggregation_qty", columnDefinition = "int4 default 0")
    int aggregationQty;

    @Column(name = "party_id")
    Long partyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", updatable = false, insertable = false)
    Party party;

    @Column(name = "aic_id")
    Long aicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aic_id", updatable = false, insertable = false)
    Aic aic;

    @Column(name = "product_serial_id")
    Long productSerialId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PARTY_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}

