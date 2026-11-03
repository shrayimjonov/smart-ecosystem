package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.factura.enums.Status;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "incoming_invoices")
@Getter
@Setter
@SQLDelete(sql = "UPDATE incoming_invoices SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class IncomingInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5193794842L;

    @Column(name = "xfile_id", unique = true)
    private Long xfileId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "signature_content")
    private String signatureContent;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String notes;
    private BigDecimal total;
    @Column(name = "send_number")
    private String sendNumber;
    @Column(name = "send_by")
    private String sendBy;
    @Column(name = "send_date")
    private LocalDateTime sendDate;
    @Column(name = "agent_approved_number")
    private String agentApprovedNumber;
    @Column(name = "agent_approved_by")
    private String agentApprovedBy;
    @Column(name = "agent_approved_date")
    private LocalDateTime agentApprovedDate;
    @Column(name = "approved_number")
    private String approvedNumber;
    @Column(name = "approved_by")
    private String approvedBy;
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @OneToOne(mappedBy = "incomingInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private IncomingInvoiceDetail invoiceDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IncomingInvoice that = (IncomingInvoice) o;
        return Objects.equals(xfileId, that.xfileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xfileId);
    }

    @Override
    public String toString() {
        return "IncomingInvoice{ xfileId= " + xfileId + '}';
    }
}
