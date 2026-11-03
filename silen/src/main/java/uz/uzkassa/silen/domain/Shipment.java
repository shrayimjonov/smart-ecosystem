package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.invoice.InvoiceBaseDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentListDTO;
import uz.uzkassa.silen.enumeration.DocumentType;
import uz.uzkassa.silen.enumeration.ShipmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Shipment.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "shipment", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "number", "customer_id", "deleted"})})
@SQLDelete(sql = "UPDATE shipment SET deleted = true WHERE id = ?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Shipment extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 65411L;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.NEW;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    private Customer customer;

    @Column(name = "store_id")
    private Long storeId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", updatable = false, insertable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "description", length = 1024)
    private String description;

    @OneToMany(mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ListInvoice> invoices;

    @OneToMany(mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<AcceptanceTransferAct> acts;

    @OneToMany(mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ShipmentItem> items;

    @Column(name = "is_comitent", columnDefinition = "bool default false")
    private boolean isCommittent;

    @Column(name = "document_type", columnDefinition = "varchar default 'INVOICE'")
    @Enumerated(EnumType.STRING)
    private DocumentType documentType = DocumentType.INVOICE;

    @Column(name = "note", length = 200)
    private String note;

    public ShipmentDTO toDto() {
        ShipmentDTO shipmentDTO = new ShipmentDTO();
        shipmentDTO.setCustomerId(getCustomerId());
        shipmentDTO.setShipmentDate(getShipmentDate());
        shipmentDTO.setNumber(getNumber());
        shipmentDTO.setCommittent(this.isCommittent());
        shipmentDTO.setDocumentType(getDocumentType());
        shipmentDTO.setNote(getNote());
        return shipmentDTO;
    }

    public ShipmentListDTO toListDto() {
        ShipmentListDTO dto = new ShipmentListDTO();
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setShipmentDate(getShipmentDate());
        dto.setComitent(this.isCommittent());
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toSelectItem());
        }
        if (getStatus() != null) {
            dto.setStatus(getStatus().toDto());
        }
        if (getInvoices() != null) {
            dto.setInvoices(getInvoices().stream().filter(listInvoice -> !listInvoice.isDeleted()).map(i -> new InvoiceBaseDTO(i.getId(), i.getNumber())).collect(Collectors.toSet()));
        }
        if (getActs() != null) {
            dto.setActs(getActs().stream().map(AcceptanceTransferAct::toSelectItem).collect(Collectors.toSet()));
        }
        if (getStore() != null) {
            dto.setStore(getStore().toSelectItem());
        }
        if (getPurchaseOrder() != null) {
            dto.setPurchaseOrders(getPurchaseOrder().toSelectItem());
        }
        dto.setNote(getNote());
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getNumber(), String.valueOf(getShipmentDate()));
    }
}
