package uz.uzkassa.smartpossupply.domain.aggregation;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationUsedStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "AggregationOrderMapper",
                classes = @ConstructorResult(
                        targetClass = AggregationStatisticsDTO.class,
                        columns = {
                                @ColumnResult(name = "draftTotal", type = Long.class),
                                @ColumnResult(name = "errorTotal", type = Long.class),
                                @ColumnResult(name = "emptyTotal", type = Long.class),
                                @ColumnResult(name = "createdTotal", type = Long.class),
                                @ColumnResult(name = "utilizedTotal", type = Long.class),
                                @ColumnResult(name = "returnedTotal", type = Long.class),
                                @ColumnResult(name = "shippedTotal", type = Long.class),
                        }
                )
        ),
        @SqlResultSetMapping(
                name = "AggregationUsedStatisticsDTOMapper",
                classes = @ConstructorResult(
                        targetClass = AggregationUsedStatisticsDTO.class,
                        columns = {
                                @ColumnResult(name = "aggregationOrderId", type = Long.class),
                                @ColumnResult(name = "quantity", type = Integer.class)
                        }
                )
        )
}
)
@Table(
        name = "aggregation",
        indexes = {
                @Index(columnList = "code", name = "aggregation_code_idx"),
                @Index(columnList = "parent_code", name = "aggregation_parent_code_idx"),
                @Index(columnList = "print_code", name = "aggregation_print_code_idx"),
                @Index(columnList = "product_id", name = "aggregation_product_id_idx"),
                @Index(columnList = "company_id", name = "aggregation_company_id_idx"),
                @Index(columnList = "code, print_code, product_id, company_id", name = "aggregation_search_keys_index"),
        }
)
@SQLDelete(sql = "UPDATE aggregation SET deleted = true WHERE id=?")
public class Aggregation extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9457844L;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "gcp")
    String gcp;

    @Column(name = "code")
    String code;

    @Column(name = "print_code")
    String originalCode;

    @Column(name = "sscc_code")
    String ssccCode;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    String description;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty")
    Integer qty;

    @Column(name = "qty_in_unit")
    Integer qtyInUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type")
    PackageType packageType = PackageType.BOX;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    AggregationStatus status;

    @Column(name = "aggregation_date")
    LocalDateTime aggregationDate;

    @Column(name = "production_date")
    LocalDate productionDate;

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    @Column(name = "checked", columnDefinition = "boolean default false")
    boolean checked = false;

    @Column(name = "report_id")
    String reportId;

    @Column(name = "aggregation_order_id")
    Long aggregationOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregation_order_id", updatable = false, insertable = false)
    AggregationOrder aggregationOrder;

    @Column(name = "parent_code")
    String parentCode;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "party_product_id")
    Long partyProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_product_id", updatable = false, insertable = false)
    PartyProduct partyProduct;

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Override
    public String getName() {
        return getOriginalCode();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGGREGATION;
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }
}
