package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "incoming_invoices_products")
@Getter
@Setter
@SQLDelete(sql = "UPDATE incoming_invoices_products SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class IncomingInvoiceProduct extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6245410737L;

    @Column(name = "ord_no")
    private String ordNo;
    @Column(name = "comittent_name")
    private String comittentName;
    @Column(name = "comittent_tin")
    private String comittentTin;
    @Column(name = "comittent_vat_reg_code")
    private String comittentVatRegCode;
    private String name;
    private String serial;
    @Column(name = "measure_id")
    private Long measureId;
    @Column(name = "base_summa")
    private BigDecimal baseSumma;
    @Column(name = "profit_rate")
    private BigDecimal profitRate;
    private BigDecimal count;
    private BigDecimal summa;
    @Column(name = "excise_rate")
    private BigDecimal exciseRate;
    @Column(name = "excise_sum")
    private BigDecimal exciseSum;
    @Column(name = "delivery_sum")
    private BigDecimal deliverySum;
    @Column(name = "vat_rate")
    private BigDecimal vatRate;
    @Column(name = "vat_sum")
    private BigDecimal vatSum;
    @Column(name = "delivery_sum_with_vat")
    private BigDecimal deliverySumWithVat;
    @Column(name = "without_vat")
    private Boolean withoutVat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incoming_invoice_detail_id")
    private IncomingInvoiceDetail incomingInvoiceDetail;
}
