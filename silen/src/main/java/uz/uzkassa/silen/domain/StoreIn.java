package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.StoreInDetailsDTO;
import uz.uzkassa.silen.dto.StoreInListDTO;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.StoreInType;
import uz.uzkassa.silen.enumeration.TransferType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "store_in")
@SQLDelete(sql = "UPDATE store_in SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StoreIn extends BaseOrganizationEntity {

    private static final long serialVersionID = 1L;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    TransferType transferType;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StockInStatus status = StockInStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private StoreInType type;

    @Column(name = "store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    @Column(name = "shipment_id")
    private String shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    private Shipment shipment;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @OneToMany(mappedBy = "storeIn")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ListInvoice> invoices;

    @OneToMany(mappedBy = "storeIn", cascade = CascadeType.ALL)
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<StoreInItem> storeInItems;

    public StoreInListDTO toListDto() {
        StoreInListDTO dto = new StoreInListDTO();
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setTransferDate(getTransferDate());
        dto.setTransferType(getTransferType());
        dto.setStatus(getStatus());
        if (getShipment() != null) {
            dto.setShipment(getShipment().toSelectItem());
        }
       /* if (CollectionUtils.isNotEmpty(getInvoices())) {
            List<InvoiceBaseDTO> invoiceBaseDTOS = getInvoices().stream().map(listInvoice -> new InvoiceBaseDTO(listInvoice.getId(), listInvoice.getNumber()))
                .collect(Collectors.toList());
            dto.setInvoices(invoiceBaseDTOS);
        }*/
        if (getStore() != null) {
            dto.setStore(getStore().toSelectItem());
        }
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toSelectItem());
            dto.setTin(getCustomer().getTin());
        }
       /* if (getOrganization() != null) {
            dto.setOrganization(getOrganization().toSelectItem());
        }*/
        dto.setLogin(getCreatedBy());
        return dto;
    }

    public StoreInDetailsDTO toDTO() {
        StoreInDetailsDTO dto = new StoreInDetailsDTO();
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setTransferDate(getTransferDate());
        dto.setTransferType(getTransferType());
        dto.setStatus(getStatus());
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toSelectItem());
        }
        if (getShipment() != null) {
            dto.setShipment(getShipment().toSelectItem());
        }
        if (getStore() != null) {
            dto.setStore(getStore().toSelectItem());
        }
        if (CollectionUtils.isNotEmpty(getStoreInItems())) {
            dto.setItems(getStoreInItems().stream().map(StoreInItem::toDto).collect(Collectors.toList()));
        }

        return dto;
    }
}
