package uz.uzkassa.horeca.domain.hall;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.hallitem.BranchFeeStatisticsDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemTableStatisticsDTO;
import uz.uzkassa.horeca.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "HallItemTableStatisticsMapper",
                        classes = {
                                @ConstructorResult(
                                        targetClass = HallItemTableStatisticsDTO.class,
                                        columns = {
                                                @ColumnResult(name = "free", type = Long.class),
                                                @ColumnResult(name = "active", type = Long.class),
                                                @ColumnResult(name = "booked", type = Long.class),
                                                @ColumnResult(name = "blocked", type = Long.class),
                                                @ColumnResult(name = "inActive", type = Long.class),
                                                @ColumnResult(name = "united", type = Long.class),
                                                @ColumnResult(name = "capacity", type = Long.class),
                                                @ColumnResult(name = "maxFee", type = Float.class),
                                                @ColumnResult(name = "minFee", type = Float.class)
                                        }
                                )
                        }
                ),
                @SqlResultSetMapping(
                        name = "BranchFeeStatisticsMapper",
                        classes = {
                                @ConstructorResult(
                                        targetClass = BranchFeeStatisticsDTO.class,
                                        columns = {
                                                @ColumnResult(name = "branchId", type = Long.class),
                                                @ColumnResult(name = "maxFee", type = Float.class),
                                                @ColumnResult(name = "minFee", type = Float.class)
                                        }
                                )
                        }
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hall_item",
        indexes = {
                @Index(columnList = "code", name = "hall_item_code_idx"),
                @Index(columnList = "hall_id", name = "hall_item_id_idx"),
                @Index(columnList = "status", name = "hall_item_status_idx"),
                @Index(columnList = "item_type", name = "hall_item_type_idx")
        })
@SQLDelete(sql = "UPDATE hall_item set deleted='true' WHERE  id=?")
public class HallItem extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4543534L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "code")
    String code;

    @Column(name = "capacity")
    Long capacity;

    @NotNull
    @Column(name = "hall_id", nullable = false)
    Long hallId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", insertable = false, updatable = false)
    Hall hall;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    HallItemStatus status = HallItemStatus.FREE;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    HallItemReason reason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "other_reason")
    String otherReason;

    @Column(name = "description")
    String description;

    @Column(name = "x")
    Integer x;

    @Column(name = "y")
    Integer y;

    @Column(name = "height")
    Integer height;

    @Column(name = "width")
    Integer width;

    @Column(name = "rotation_angle")
    Integer rotationAngle;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    HallItemType itemType = HallItemType.TABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "shape")
    ShapeType shape;

    @Column(name = "number_of_guest")
    Long numberOfGuest;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false' and status in ('NEW','ACTIVE')")
    @OrderBy("fromDate")
    List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false' and status in ('NEW','ACTIVE')")
    @OrderBy("orderDate")
    List<Order> orders = new ArrayList<>();

    @Column(name = "merged_table_id")
    Long mergedTableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_table_id", updatable = false, insertable = false)
    HallItem mergedTable;

    @OneToMany(mappedBy = "mergedTable", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted =false")
    List<HallItem> mergedTables = new ArrayList<>();

    @Column(name = "client_id")
    Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    User client;

    @Column(name = "with_order", columnDefinition = "boolean default false")
    boolean withOrder = false;

    @Column(name = "fee_percent", scale = 2, precision = 25)
    BigDecimal feePercent;

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getCode());
    }

    public void setStatus(HallItemStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HallItem)) {
            return false;
        }
        return id != null && id.equals(((HallItem) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HALL_ITEM;
    }
}
