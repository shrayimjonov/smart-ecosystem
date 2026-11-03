package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.eventhandler.ActOfTransmissionInvoiceDateToPersistListener;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "act_of_transmission_invoice", indexes = {
        @Index(columnList = "x_file_id", name = "act_of_transmission_x_file_id_index")
})
@Entity
@SQLDelete(sql = "UPDATE act_of_transmission_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
//to change invoice date on create time
@EntityListeners(ActOfTransmissionInvoiceDateToPersistListener.class)
public class ActOfTransmissionInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2093302529L;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "total_product_count")
    private Integer totalProductCount;

    @Column(name = "total_sell_price")
    private BigDecimal totalSellPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_vat_price")
    private BigDecimal totalVatPrice;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "x_file_id")
    private String xfileId;

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ActOfTransmissionInvoiceDetail> details = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private ContractForLeasing contract;

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private ActOfTransmission actOfTransmission;

    public void addDetail(ActOfTransmissionInvoiceDetail detail) {
        detail.setInvoice(this);
        details.add(detail);
    }

    public String getNumber() {
        return PrefixEnum.CLI + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
