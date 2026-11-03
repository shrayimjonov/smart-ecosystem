package uz.uzkassa.smartpossupply.domain.aggregation;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderListDTO;
import uz.uzkassa.smartpossupply.enums.AggregationOrderStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigInteger;
import java.time.LocalDateTime;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "AggregationOrderListMapper",
                        classes = @ConstructorResult(
                                targetClass = AggregationOrderListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "orderNumber", type = String.class),
                                        @ColumnResult(name = "packageType", type = String.class),
                                        @ColumnResult(name = "qty", type = Integer.class),
                                        @ColumnResult(name = "used", type = BigInteger.class),
                                        @ColumnResult(name = "createdDate", type = LocalDateTime.class),
                                }
                        )
                ),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "aggregation_order")
@SQLDelete(sql = "UPDATE aggregation_order SET deleted = true WHERE id=?")
public class AggregationOrder extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 831147L;

    @Column(name = "order_number")
    String orderNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    AggregationOrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type")
    PackageType packageType;

    @Column(name = "qty")
    Integer qty;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "for_employee_id")
    Long forEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_employee_id", updatable = false, insertable = false)
    User forEmployee;

    @Override
    public String getName() {
        return this.orderNumber;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGGREGATION_ORDER;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
