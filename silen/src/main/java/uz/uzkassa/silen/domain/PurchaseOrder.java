package uz.uzkassa.silen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDetailDTO;
import uz.uzkassa.silen.enumeration.PurchaseOrderStatus;
import uz.uzkassa.silen.enumeration.PurchaseOrderType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A PurchaseOrder.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "purchase_order", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"from_organization_id", "number"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE purchase_order SET deleted = 'true' WHERE id=?")
public class PurchaseOrder extends BaseIdentityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "number")
    private String number;
    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(name = "from_organization_id")
    private String fromOrganizationId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_organization_id", updatable = false, insertable = false)
    private Organization fromOrganization;

    @Column(name = "customer_id")
    private Long customerId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    @SQLRestriction("deleted is not true")
    private Customer customer;

    @Column(name = "store_id")
    private Long storeId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    @SQLRestriction("deleted is not true")
    private Store store;

    @Column(name = "contract_id")
    private String contractId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    @SQLRestriction("deleted is not true")
    private Contract contract;

    @OneToMany(mappedBy = "purchaseOrder", orphanRemoval = true, cascade = CascadeType.ALL)
    @SQLRestriction("deleted is not true")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Shipment> shipments;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderType type = PurchaseOrderType.SELF;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "order", orphanRemoval = true)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PurchaseOrderProduct> products = new HashSet<>();

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(String.valueOf(this.getId()), this.getNumber());
    }

    public PurchaseOrderDetailDTO toDto() {
        PurchaseOrderDetailDTO dto = new PurchaseOrderDetailDTO();
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setOrderDate(getOrderDate());
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toSelectItem());
        }
        if (getStore() != null) {
            dto.setStore(getStore().toSelectItem());
        }
        if (getContract() != null) {
            dto.setContract(getContract().toSelectItem());
        }
        if (getStatus() != null) {
            dto.setStatus(getStatus().toDto());
        }
        if (getType() != null) {
            dto.setType(getType().toDto());
        }
        if (getShipments() != null) {
            dto.setShipments(getShipments().stream().map(Shipment::toSelectItem).collect(Collectors.toList()));
        }
        dto.setProducts(getProducts().stream().map(PurchaseOrderProduct::toDto).collect(Collectors.toSet()));
        return dto;
    }
}
