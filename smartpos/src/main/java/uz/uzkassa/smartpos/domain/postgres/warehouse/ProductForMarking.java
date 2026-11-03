package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "product_for_marking")
@NoArgsConstructor
public class ProductForMarking implements Serializable {
    @Id
    private String marking;
    private String parent;
    private Long productId;
    private Long branchId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private HashSet<String> children;
    @Enumerated(EnumType.STRING)
    private MarkingStatus status;
    private String errorMessage;
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    private Long incomeOfProductId;

    public ProductForMarking(String marking, Long productId, Long branchId, Long incomeOfProductId) {
        this.marking = marking;
        this.productId = productId;
        this.branchId = branchId;
        this.incomeOfProductId = incomeOfProductId;
    }

    public Set<String> getChildren() {
        if (children == null) return new HashSet<>();
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductForMarking that = (ProductForMarking) o;
        return Objects.equals(marking, that.marking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marking);
    }
}
