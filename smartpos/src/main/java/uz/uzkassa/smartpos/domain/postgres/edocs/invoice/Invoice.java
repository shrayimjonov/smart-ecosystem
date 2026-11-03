package uz.uzkassa.smartpos.domain.postgres.edocs.invoice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.dto.eimzo.SignatureData;
import uz.uzkassa.smartpos.dto.factura.FacturaDTO;
import uz.uzkassa.smartpos.dto.factura.InvoiceDTO;
import uz.uzkassa.smartpos.enums.ImportMarkStatus;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "factura_id", length = 24, unique = true)
    private String facturaId;
    @Column(name = "factura_product_id", length = 24)
    private String facturaProductId;
    @Column(name = "number")
    private String number;
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "seller_tin")
    private String sellerTin;
    @Column(name = "seller_name")
    private String sellerName;
    @Column(name = "buyer_tin")
    private String buyerTin;
    @Column(name = "buyer_name")
    private String buyerName;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "signature_content")
    private String signatureContent;

    @Column(name = "total", precision = 25, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "import_mark_status", length = 20)
    private ImportMarkStatus importMarkStatus;

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

    @Column(name = "invoice_id", unique = true)
    private Long invoiceId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", updatable = false, insertable = false)
    private DraftInvoice invoice;
    @OneToMany(mappedBy = "invoice")
    private List<Shipment> shipments;

    public FacturaDTO toFacturaDTO() {
        if (getInvoice() != null) {
            FacturaDTO facturaDTO = getInvoice().toFacturaDTO();
            facturaDTO.setFacturaId(getFacturaId());
            return facturaDTO;
        }
        return null;
    }

    public InvoiceDTO toDto(boolean brief) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(getId());
        dto.setFacturaId(getFacturaId());
        dto.setNumber(getNumber());
        dto.setInvoiceDate(getInvoiceDate());
        dto.setCreatedDate(getCreatedDate());
        dto.setLastModifiedDate(getLastModifiedDate());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getRuText());
        }
        dto.setNotes(getNotes());
        dto.setTotal(getTotal());
        dto.setSellerTin(getSellerTin());
        dto.setSellerName(getSellerName());
        dto.setBuyerTin(getBuyerTin());
        dto.setBuyerName(getBuyerName());
        dto.setSendDate(getSendDate());
        dto.setApprovedDate(getApprovedDate());
        dto.setImportMarkStatus(getImportMarkStatus());

        if (!brief) {
            dto.setSendNumber(getSendNumber());
            dto.setSendBy(getSendBy());

            dto.setApprovedNumber(getApprovedNumber());
            dto.setApprovedBy(getApprovedBy());

            dto.setNotes(getNotes());
            dto.setSignatureContent(getSignatureContent());
        }
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
}
