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
@Table(name = "rent_invoice", indexes = {
        @Index(columnList = "x_file_id", name = "rent_x_file_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE rent_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1951186779L;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "total_sell_price")
    private BigDecimal totalSellPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_vat_price")
    private BigDecimal totalVatPrice;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private RentOffer rentOffer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentInvoiceDetail> details = new ArrayList<>();

    @Column(name = "x_file_id")
    private String xfileId;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    public String getNumber() {
        return PrefixEnum.IR + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }

    public void addDetail(RentInvoiceDetail detail) {
        detail.setInvoice(this);
        details.add(detail);
    }

    public String createDescriptionForLedger(){
        return " Договор № " +
                rentOffer.getNumber() +
                " от " + rentOffer.getCreatedDate() + " г." +
                " Общ.сумма " + getTotalSellPrice() +
                " счет фактура  №" + getNumber() +
                " от " + getInvoiceDate() + " г.";
    }

}
