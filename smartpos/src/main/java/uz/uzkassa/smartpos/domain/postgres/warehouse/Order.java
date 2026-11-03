package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.common.Address;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;
import uz.uzkassa.smartpos.integration.enums.PaymentType;
import uz.uzkassa.smartpos.integration.enums.SaleType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 10900323L;
    @Id
    private Long id;
    String orderNumber;

    LocalDateTime orderDate = LocalDateTime.now();

    Long contractId;

    @NotNull
    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @NotNull
    Long supplierId;
    @NotNull
    String supplierTin;
    @NotNull
    String supplierName;

    Long branchId;
    String branchName;

    Long warehouseId;

    @Enumerated(EnumType.STRING)
    @NotNull
    OrderStatus status = OrderStatus.NEW;

    @Enumerated(EnumType.STRING)
    SaleType saleType;

    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "delivery_branch_id")
    Long deliveryBranchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_branch_id", updatable = false, insertable = false)
    Branch deliveryBranch;

    @Embedded
    Address deliveryAddress;

    LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    DeliveryType deliveryType;

    @NotNull
    BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderProduct> products = new ArrayList<>();

    public void addProduct(OrderProduct product) {
        product.setOrder(this);
        this.getProducts().add(product);
    }

    public void setProducts(List<OrderProduct> products) {
        products.forEach(this::addProduct);
    }

    @Override
    public String getName() {
        return this.getOrderNumber();
    }

    public CommonDTO toCommonDTO() {
        return new CommonDTO(getId(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
