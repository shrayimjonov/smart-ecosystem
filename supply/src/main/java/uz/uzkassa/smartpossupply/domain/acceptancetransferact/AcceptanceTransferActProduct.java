package uz.uzkassa.smartpossupply.domain.acceptancetransferact;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Set;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "acceptance_transfer_act_product",
        indexes = {
                @Index(columnList = "product_id", name = "acceptance_transfer_act_product_product_idx"),
                @Index(columnList = "catalog_code", name = "acceptance_transfer_act_product_catalog_code_idx"),
                @Index(columnList = "barcode", name = "acceptance_transfer_act_product_barcode_idx"),
                @Index(columnList = "acceptance_transfer_act_detail_id", name = "acceptance_transfer_act_product_detail_id_idx")
        }
)
public class AcceptanceTransferActProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 73873492398L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "ord_no")
    Integer ordNo;

    @Column(name = "name", length = 2000)
    String name;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name", length = 2000)
    String catalogName;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "summa", precision = 25, scale = 2)
    BigDecimal summa;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "delivery_sum", precision = 25, scale = 2)
    BigDecimal deliverySum;

    @Deprecated
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "acceptance_transfer_act_marks")
    Set<String> marks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "act_marks")
    Set<String> actMarks;

    @Deprecated
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "acceptance_transfer_act_aggregations")
    Set<String> aggregations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "act_aggregations")
    Set<String> actAggregations;

    @Deprecated
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "acceptance_transfer_act_blocks")
    Set<String> blocks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "act_blocks")
    Set<String> actBlocks;

    @Column(name = "acceptance_transfer_act_detail_id")
    Long acceptanceTransferActDetailId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptance_transfer_act_detail_id", updatable = false, insertable = false)
    AcceptanceTransferActDetail acceptanceTransferActDetail;

    public Set<String> getMarks() {
        if (CollectionUtils.isNotEmpty(this.actMarks)) {
            return this.actMarks;
        } else {
            Hibernate.initialize(this.marks);
            return marks;
        }
    }

    public Set<String> getAggregations() {
        if (CollectionUtils.isNotEmpty(this.actAggregations)) {
            return this.actAggregations;
        } else {
            Hibernate.initialize(this.aggregations);
            return aggregations;
        }
    }

    public Set<String> getBlocks() {
        if (CollectionUtils.isNotEmpty(this.actBlocks)) {
            return this.actBlocks;
        } else {
            Hibernate.initialize(this.blocks);
            return blocks;
        }
    }

    public void setMarks(Set<String> marks) {
        this.actMarks = marks;
    }

    public void setAggregations(Set<String> aggregations) {
        this.actAggregations = aggregations;
    }

    public void setBlocks(Set<String> blocks) {
        this.actBlocks = blocks;
    }
}
