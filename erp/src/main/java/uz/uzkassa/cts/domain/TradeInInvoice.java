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
import uz.uzkassa.cts.eventhandler.TradeInInvoiceDateToPersistListener;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_in_invoice", indexes = {
        @Index(columnList = "x_file_id", name = "trade_in_x_file_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(TradeInInvoiceDateToPersistListener.class)
public class TradeInInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6501135700L;

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
    @JoinColumn(name = "contract_id")
    private TradeInContract contract;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_act_id")
    private TradeInAct tradeInAct;

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInInvoiceDetail detail;

    @Column(name = "x_file_id")
    private String xfileId;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    public String getNumber() {
        return PrefixEnum.ITI + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
