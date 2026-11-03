package uz.uzkassa.silen.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.acceptancetransferact.*;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActDetailDTO;
import uz.uzkassa.silen.enumeration.ActStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "acceptance_transfer_act")
@SQLDelete(sql = "UPDATE acceptance_transfer_act SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLRestriction("deleted is not true")
public class AcceptanceTransferAct extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 12345234524352L;

    @Column(name = "act_id")
    String actId;

    @Column(name = "act_number")
    String actNumber;

    @Column(name = "act_date")
    LocalDate actDate;

    @Column(name = "contract_id")
    String contractId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    Contract contract;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    private Customer customer;

    @Column(name = "store_id")
    Long storeId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    @ColumnDefault("0.00")
    @Column(name = "comission", precision = 25, scale = 2)
    BigDecimal comission = BigDecimal.ZERO;
    @ColumnDefault("0.00")
    @Column(name = "total_cost", precision = 25, scale = 2)
    BigDecimal totalCost = BigDecimal.ZERO;
    @ColumnDefault("0.00")
    @Column(name = "total_discount", precision = 25, scale = 2)
    BigDecimal totalDiscount = BigDecimal.ZERO;
    @ColumnDefault("0.00")
    @Column(name = "total_payment", precision = 25, scale = 2)
    BigDecimal totalPayment = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "text")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ActStatus status = ActStatus.DRAFT;

    @OneToMany(mappedBy = "acceptanceTransferAct", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @SQLRestriction("deleted is not true")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    List<AcceptanceTransferActProduct> products = new LinkedList<>();

    @Column(name = "shipment_id")
    String shipmentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    private Shipment shipment;

    @Column(name = "supplier_branch_id")
    Long supplierBranchId;
    @Column(name = "reject_note")
    String rejectNote;

    public void addProduct(AcceptanceTransferActProduct product) {
        product.setAcceptanceTransferAct(this);
        this.products.add(product);
    }

    public AcceptanceTransferActDTO toActDTO() {
        AcceptanceTransferActDTO acceptanceTransferActDTO = new AcceptanceTransferActDTO();
        acceptanceTransferActDTO.setSilenId(this.getId());
        acceptanceTransferActDTO.setSellerPinfl(this.getOrganization().getTin());
        if (this.getCustomer() != null) {
            acceptanceTransferActDTO.setBuyerTinOrPinfl(this.getCustomer().getTin());
        }
        if (this.getStore() != null) {
            acceptanceTransferActDTO.setBuyerTinOrPinfl(this.getOrganization().getTin());
        }

        acceptanceTransferActDTO.setTotalPrice(this.getTotalCost());

        acceptanceTransferActDTO.setSeller(this.toSellerDTO());
        acceptanceTransferActDTO.setBuyer(this.toBuyerDTO());
        acceptanceTransferActDTO.setAcceptanceTransferActDoc(this.toActDocDTO());
        if (this.getContract() != null) {
            acceptanceTransferActDTO.setContractDoc(this.toContractDTO());
        }
        return acceptanceTransferActDTO;
    }

    public AcceptanceTransferActDocDTO toActDocDTO() {
        AcceptanceTransferActDocDTO acceptanceTransferActDocDTO = new AcceptanceTransferActDocDTO();
        acceptanceTransferActDocDTO.setAcceptanceTransferActNo(this.getActNumber());
        if (this.getActDate() != null) {
            acceptanceTransferActDocDTO.setAcceptanceTransferActDate(this.getActDate().toString());
        }
        return acceptanceTransferActDocDTO;
    }

    public AcceptanceTransferActContractDocDTO toContractDTO() {
        AcceptanceTransferActContractDocDTO acceptanceTransferActContractDocDTO = new AcceptanceTransferActContractDocDTO();
        acceptanceTransferActContractDocDTO.setContractNo(this.getContract().getNumber());
        if (this.getContract().getDate() != null) {
            acceptanceTransferActContractDocDTO.setContractDate(this.getContract().getDate().toString());
        }
        return acceptanceTransferActContractDocDTO;
    }


    public AcceptanceTransferActSellerDTO toSellerDTO() {
        AcceptanceTransferActSellerDTO acceptanceTransferActSellerDTO = new AcceptanceTransferActSellerDTO();
        acceptanceTransferActSellerDTO.setName(this.getOrganization().getName());
        acceptanceTransferActSellerDTO.setAddress(this.getOrganization().getAddress());
        acceptanceTransferActSellerDTO.setAccount(this.getOrganization().getAccount());
        return acceptanceTransferActSellerDTO;
    }

    public AcceptanceTransferActBuyerDTO toBuyerDTO() {
        AcceptanceTransferActBuyerDTO acceptanceTransferActBuyerDTO = new AcceptanceTransferActBuyerDTO();
        if(getCustomer() != null) {
            acceptanceTransferActBuyerDTO.setName(this.getCustomer().getName());
        }
        if(getStore() != null) {
            acceptanceTransferActBuyerDTO.setName(this.getStore().getName());
            acceptanceTransferActBuyerDTO.setBranchCode(getStore().getBranchCode());
        }
        acceptanceTransferActBuyerDTO.setAddress("");
        acceptanceTransferActBuyerDTO.setAccount("");
        acceptanceTransferActBuyerDTO.setVatRegCode("");
        acceptanceTransferActBuyerDTO.setVatRegStatus(null);
        acceptanceTransferActBuyerDTO.setSupplierBranchId(getSupplierBranchId());
        if(getStore() != null) {
            acceptanceTransferActBuyerDTO.setBranchCode(getStore().getBranchCode());
        }
        return acceptanceTransferActBuyerDTO;
    }

    public AcceptanceTransferActDetailDTO toDTO() {
        AcceptanceTransferActDetailDTO dto = new AcceptanceTransferActDetailDTO();
        dto.setId(getId());
        dto.setActNumber(getActNumber());
        dto.setActDate(getActDate());
        dto.setComission(getComission());
        dto.setCreatedDate(getCreatedDate());
        dto.setDescription(getDescription());
        dto.setActId(getActId());
        dto.setTotalCost(getTotalCost());
        dto.setTotalDiscount(getTotalDiscount());
        dto.setTotalPayment(getTotalPayment());
        dto.setStatus(getStatus().toSelectItem());
        dto.setSupplierBranchId(getSupplierBranchId());
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toDto());
        }
        if (getStore() != null) {
            dto.setStore(getStore().toDTO());
        }
        if (getOrganization() != null) {
            dto.setSupplier(getOrganization().toFacturaCustomer());
        }
        if (getContract() != null) {
            dto.setContract(getContract().toSelectItem());
        }
        return dto;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getActNumber());
    }
}
