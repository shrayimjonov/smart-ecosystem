package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.product.Product;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "compensation",
        indexes = {
                @Index(columnList = "product_id", name = "compensation_product_id_idx"),
                @Index(columnList = "price", name = "compensation_price_idx"),
                @Index(columnList = "percent", name = "compensation_percent_idx"),
                @Index(columnList = "branch_id", name = "compensation_branch_id_idx")
        })
@SQLDelete(sql = "UPDATE compensation set deleted='true' WHERE  id=?")
public class Compensation extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 758437L;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "percent")
    BigDecimal percent;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @NotNull
    @Column(name = "category_id", nullable = false)
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    Category category;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Compensation)) {
            return false;
        }
        return id != null && id.equals(((Compensation) o).id);
    }
}
