package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale_invoice")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sale_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4728936695L;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_contract_id")
    private SaleContract saleContract;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_act_id")
    private SaleAct saleAct;

    @Column(name = "x_file_id")
    private String xfileId;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    @OneToMany(mappedBy = "saleInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleInvoiceDetail> details = new ArrayList<>();

    public String getNumber() {
        return PrefixEnum.IAS + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }

    public void addDetail(SaleInvoiceDetail detail) {
        detail.setSaleInvoice(this);
        details.add(detail);
    }
}
