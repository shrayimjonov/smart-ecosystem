package uz.uzkassa.silen.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.eimzo.SignatureData;
import uz.uzkassa.silen.dto.invoice.FacturaDTO;
import uz.uzkassa.silen.dto.invoice.InvoiceDTO;
import uz.uzkassa.silen.enumeration.AsilBelgiDocStatus;
import uz.uzkassa.silen.enumeration.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "list_invoice",
        indexes = {
                @Index(columnList = "shipment_id", name = "invoice_shipment_idx"),
                @Index(columnList = "status", name = "invoice_status_idx"),
                @Index(columnList = "status,seller_tin", name = "invoice_sta_sel_idx"),
                @Index(columnList = "number", name = "list_invoice_number_index")
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@SQLDelete(sql = "UPDATE list_invoice SET deleted = true WHERE id = ?")
@DynamicUpdate
public class ListInvoice extends BaseIdentityEntity implements Serializable {
    @Column(name = "factura_id", length = 24, unique = true)
    private String facturaId;
    @Column(name = "factura_product_id", length = 24)
    private String facturaProductId;
    @Column(name = "number")
    private String number;
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "invoice_id", unique = true)
    private Long invoiceId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", updatable = false, insertable = false)
    private Invoice invoice;

    @Column(name = "seller_tin", nullable = false)
    private String sellerTin;
    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "buyer_tin")
    private String buyerTin;
    @Column(name = "buyer_name")
    private String buyerName;
    @Column(name = "buyer_account")
    private String buyerAccount;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "signature_content")
    private String signatureContent;

    @Column(name = "total", precision = 25, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.DRAFT;

    @Column(name = "notes")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String notes;

    @Column(name = "send_number")
    private String sendNumber;
    @Column(name = "send_by")
    private String sendBy;
    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "approved_number")
    private String approvedNumber;
    @Column(name = "approved_by")
    private String approvedBy;
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;
    @Column(name = "error_message")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String errorMessage;

    @Column(name = "shipment_id")
    private String shipmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    private Shipment shipment;

    // for return from shipment
    @Column(name = "store_in_id")
    private String storeInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_in_id", updatable = false, insertable = false)
    private StoreIn storeIn;

    @Column(name = "turon_doc_id")
    private String turonDocId;

    @Enumerated(EnumType.STRING)
    @Column(name = "turon_status", length = 20)
    private AsilBelgiDocStatus turonStatus;

    public InvoiceDTO toListDto(boolean revert) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setInvoiceDate(getInvoiceDate());
        dto.setSellerTin(getSellerTin());
        dto.setSellerName(getSellerName());
        dto.setBuyerTin(getBuyerTin());
        dto.setBuyerName(getBuyerName());
        dto.setContrAgentTIN(revert ? getSellerTin() : getBuyerTin());
        dto.setContrAgentName(revert ? getSellerName() : getBuyerName());
        dto.setCreatedDate(getCreatedDate());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getShortText());
        }
        dto.setNotes(getNotes());
        dto.setTotal(getTotal());
        dto.setErrorMessage(getErrorMessage());
        if (getShipment() != null) {
            dto.setShipmentId(getShipmentId());
            dto.setShipmentNumber(getShipment().getNumber());
        }
        if (getTuronStatus() != null) {
            dto.setTuronStatus(getTuronStatus());
            dto.setTuronStatusName(getTuronStatus().getText());
        }
        dto.setTuronDocId(getTuronDocId());
        return dto;
    }

    public InvoiceDTO toDto() {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(getId());
        dto.setCreatedDate(getCreatedDate());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getShortText());
        }
        dto.setNotes(getNotes());
        dto.setTotal(getTotal());
        dto.setErrorMessage(getErrorMessage());

        dto.setSendDate(getSendDate());
        dto.setSendNumber(getSendNumber());
        dto.setSendBy(getSendBy());

        dto.setApprovedDate(getApprovedDate());
        dto.setApprovedNumber(getApprovedNumber());
        dto.setApprovedBy(getApprovedBy());

        dto.setSignatureContent(getSignatureContent());
        if (getTuronStatus() != null) {
            dto.setTuronStatus(getTuronStatus());
            dto.setTuronStatusName(getTuronStatus().getText());
        }
        dto.setTuronDocId(getTuronDocId());
        return dto;
    }

    public FacturaDTO toFacturaDto() {
        if (getInvoice() != null) {
            FacturaDTO facturaDTO = getInvoice().toFacturaDTO();
            facturaDTO.setFacturaId(getFacturaId());
            facturaDTO.setSellerTin(getSellerTin());
            facturaDTO.setBuyerTin(getBuyerTin());
            facturaDTO.setBuyerAccount(getBuyerAccount());
            return facturaDTO;
        }
        return null;
    }

    public InvoiceDTO to1cDto() {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(getId());
        dto.setFacturaId(getFacturaId());
        dto.setNumber(getNumber());
        dto.setInvoiceDate(getInvoiceDate());
        dto.setCreatedDate(getCreatedDate());
        dto.setLastModifiedDate(getLastModifiedDate());
        dto.setStatus(getStatus());
        dto.setNotes(getNotes());

        dto.setSendDate(getSendDate());
        dto.setSendNumber(getSendNumber());
        dto.setSendBy(getSendBy());

        dto.setApprovedDate(getApprovedDate());
        dto.setApprovedNumber(getApprovedNumber());
        dto.setApprovedBy(getApprovedBy());
        return dto;
    }

    public void setSenderData(SignatureData signatureData) {
        setSendNumber(signatureData.getNumber());
        setSendBy(signatureData.getOwner());
        setSendDate(signatureData.getDateTime());
    }

    public void setApproverData(SignatureData signatureData) {
        setApprovedNumber(signatureData.getNumber());
        setApprovedBy(signatureData.getOwner());
        setApprovedDate(signatureData.getDateTime());
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.invoiceId.toString(), getNumber(), getTotal());
    }

    public SelectItem toSelectItem1() {
        return new SelectItem(getFacturaId(), getNumber(), getInvoiceDate().toString(), getTotal());
    }
}
