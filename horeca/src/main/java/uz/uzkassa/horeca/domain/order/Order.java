package uz.uzkassa.horeca.domain.order;

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
import uz.uzkassa.horeca.domain.Bill;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailMapperDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "OrderKanbanBoardDetailMapper",
                        classes = {
                                @ConstructorResult(
                                        targetClass = OrderKanbanBoardDetailMapperDTO.class,
                                        columns = {
                                                @ColumnResult(name = "id", type = Long.class),
                                                @ColumnResult(name = "orderNumber", type = String.class),
                                                @ColumnResult(name = "orderDate", type = LocalDateTime.class),
                                                @ColumnResult(name = "comment", type = String.class),
                                                @ColumnResult(name = "orderStatus", type = String.class),
                                                @ColumnResult(name = "tableId", type = Long.class),
                                                @ColumnResult(name = "tableName", type = String.class),
                                                @ColumnResult(name = "hallId", type = Long.class),
                                                @ColumnResult(name = "hallName", type = String.class),
                                                @ColumnResult(name = "itemId", type = Long.class),
                                                @ColumnResult(name = "qty", type = BigDecimal.class),
                                                @ColumnResult(name = "itemComment", type = String.class),
                                                @ColumnResult(name = "status", type = String.class),
                                                @ColumnResult(name = "itemOrderDate", type = LocalDateTime.class),
                                                @ColumnResult(name = "lastModifiedDate", type = LocalDateTime.class),
                                                @ColumnResult(name = "marks", type = String.class),
                                                @ColumnResult(name = "chefId", type = Long.class),
                                                @ColumnResult(name = "chefFirstName", type = String.class),
                                                @ColumnResult(name = "chefLastName", type = String.class),
                                                @ColumnResult(name = "waiterId", type = Long.class),
                                                @ColumnResult(name = "waiterFirstName", type = String.class),
                                                @ColumnResult(name = "waiterLastName", type = String.class),
                                                @ColumnResult(name = "menuItemId", type = Long.class),
                                                @ColumnResult(name = "menuItemName", type = String.class),
                                                @ColumnResult(name = "multiUnitId", type = Long.class),
                                                @ColumnResult(name = "weight", type = Double.class),
                                                @ColumnResult(name = "price", type = BigDecimal.class),
                                                @ColumnResult(name = "unitId", type = Long.class),
                                                @ColumnResult(name = "unitName", type = String.class),
                                                @ColumnResult(name = "techCardId", type = Long.class),
                                                @ColumnResult(name = "techCardName", type = String.class),
                                                @ColumnResult(name = "hasMark", type = Boolean.class),
                                        }
                                ),
                        }
                ),
                @SqlResultSetMapping(
                        name = "OrderKanbanBoardListMapper",
                        classes = {
                                @ConstructorResult(
                                        targetClass = OrderKanbanBoardListDTO.class,
                                        columns = {
                                                @ColumnResult(name = "id", type = Long.class),
                                                @ColumnResult(name = "orderNumber", type = String.class),
                                                @ColumnResult(name = "orderDate", type = LocalDateTime.class),
                                                @ColumnResult(name = "hallId", type = Long.class),
                                                @ColumnResult(name = "hallName", type = String.class),
                                                @ColumnResult(name = "tableId", type = Long.class),
                                                @ColumnResult(name = "tableName", type = String.class),
                                                @ColumnResult(name = "totalCount", type = BigDecimal.class),
                                                @ColumnResult(name = "completedCount", type = BigDecimal.class),
                                        }
                                ),
                        }
                ),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders",
        indexes = {
                @Index(columnList = "order_date", name = "orders_date_idx"),
                @Index(columnList = "table_id", name = "orders_table_id_idx"),
                @Index(columnList = "client_id", name = "orders_client_id_idx"),
                @Index(columnList = "waiter_id", name = "orders_waiter_id_idx"),
                @Index(columnList = "branch_id", name = "orders_branch_id_idx"),
                @Index(columnList = "status", name = "orders_status_idx")
        })
@SQLDelete(sql = "UPDATE orders set deleted='true' where id=?")
public class Order extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 235610L;

    @NotNull
    @Column(name = "uid", length = 50, unique = true, nullable = false)
    String uid;

    @Column(name = "order_number")
    String orderNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "table_id")
    Long tableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", insertable = false, updatable = false)
    HallItem table;

    @Column(name = "client_id")
    Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    User client;

    @Column(name = "waiter_id")
    Long waiterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id", insertable = false, updatable = false)
    User waiter;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "shift_id")
    Long shiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", insertable = false, updatable = false)
    Shift shift;

    @Column(name = "reservation_id")
    Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", insertable = false, updatable = false)
    Reservation reservation;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.NEW;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    OrderReason reason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "other_reason")
    String otherReason;

    @Column(name = "number_of_guest")
    Long numberOfGuest;

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "total_selling_price", scale = 2, precision = 25)
    BigDecimal totalSellingPrice;

    @Column(name = "total_qty", scale = 2, precision = 25)
    BigDecimal totalQty;

    @Column(name = "total_vat", scale = 2, precision = 25)
    BigDecimal totalVat;

    @Column(name = "fee_percent", scale = 2, precision = 25)
    BigDecimal feePercent;

    @Column(name = "fee_amount", scale = 2, precision = 25)
    BigDecimal feeAmount;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<OrderPayment> payments = new ArrayList<>();

    @Deprecated
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "comment")
    String comment;

    @Column(name = "receipt_id")
    Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", updatable = false, insertable = false)
    Receipt receipt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false' and status not in ('CANCELLED')")
    @OrderBy("id")
    List<OrderItem> items = new ArrayList<>();

    @Column(name = "items_count")
    Integer itemsCount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    List<Bill> bills = new ArrayList<>();

    @Override
    public String getName() {
        return this.orderNumber;
    }

    public CommonDTO clientCommonDTO() {
        if (getClientId() != null) {
            return new CommonDTO(getClientId(), Optional.ofNullable(getClient().getGuestName()).orElse(getClient().getName()));
        }
        return null;
    }

    public void addItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItem.setOrderId(this.id);
        items.add(orderItem);
    }

    public void addPayment(OrderPayment orderPayment) {
        orderPayment.setOrder(this);
        orderPayment.setOrderId(this.id);
        payments.add(orderPayment);
    }

    public void setOrderDate(LocalDateTime orderDate) {
        addLogHistory("orderDate", this.orderDate, orderDate);
        this.orderDate = orderDate;
    }

    public void setTable(HallItem table) {
        addLogHistory("table", this.table != null ? this.table.getName() : null, table != null ? table.getName() : null);
        this.table = table;
    }

    public void setStatus(OrderStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setReason(OrderReason reason) {
        addLogHistory("reason", this.reason != null ? this.reason.getName() : null, reason != null ? reason.getName() : null);
        this.reason = reason;
    }

    public void setOtherReason(String otherReason) {
        addLogHistory("otherReason", this.otherReason, otherReason);
        this.otherReason = otherReason;
    }

    public void setComment(String comment) {
        addLogHistory("catalogName", this.comment, comment);
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER;
    }
}
