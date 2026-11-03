package uz.uzkassa.horeca.domain.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailMapperDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemChefStatsMapperDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.OrderItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SqlResultSetMappings(
    {
        @SqlResultSetMapping(
            name = "OrderItemChefStatsMapper",
            classes = {
                @ConstructorResult(
                    targetClass = OrderItemChefStatsMapperDTO.class,
                    columns = {
                            @ColumnResult(name = "preparing", type = Long.class),
                            @ColumnResult(name = "ready", type = Long.class),
                            @ColumnResult(name = "cancelled", type = Long.class),
                    }
                ),
            }
        ),
    }
)
@Getter
@Setter
@Table(name = "order_item",
        indexes = {
                @Index(columnList = "menu_item_id", name = "order_item_menu_item_id_idx"),
                @Index(columnList = "multi_unit_id", name = "order_item_multi_unit_id_idx"),
                @Index(columnList = "chef_id", name = "order_item_chef_id_idx"),
                @Index(columnList = "order_id", name = "order_item_order_id_idx")
        })
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE order_item SET deleted='true' WHERE id=?")
public class OrderItem extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 234430012L;

    @NotNull
    @Column(name = "menu_item_id", nullable = false)
    Long menuItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    MenuItem menuItem;

    @NotNull
    @Column(name = "multi_unit_id", nullable = false)
    Long multiUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_unit_id", insertable = false, updatable = false)
    MultiUnit multiUnit;

    @Column(name = "chef_id")
    Long chefId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_id", insertable = false, updatable = false)
    User chef;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "vat_rate", scale = 2, precision = 25)
    BigDecimal vatRate;

    @Column(name = "vat_price", scale = 2, precision = 25)
    BigDecimal vatPrice;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    OrderItemStatus status = OrderItemStatus.NEW;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "comment")
    String comment;

    @NotNull
    @Column(name = "order_id", nullable = false)
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Column(name = "order_date")
    LocalDateTime orderDate = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Set<String> marks=new HashSet<>();

    public void addMark(String code){
        this.marks.add(code);
    }

    public void addMark(Set<String> codes) {
        this.marks.addAll(codes);
    }

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }
}
