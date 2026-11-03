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
import uz.uzkassa.cts.eventhandler.OnetimeOrderInvoiceDateToPersistListener;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "one_time_order_invoice", indexes = {
        @Index(columnList = "x_file_id", name = "one_time_order_x_file_id_index")
})
@Entity
@SQLDelete(sql = "UPDATE one_time_order_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
//to change invoice date on create time
@EntityListeners(OnetimeOrderInvoiceDateToPersistListener.class)
public class OneTimeOrderInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5487973423L;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OneTimeOrder order;

    @Column(name = "file_uri", length = 500)
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

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

    @Column(name = "total_vat_price_in_word")
    private String totalVatPriceInWord;

    @Column(name = "total_sell_price_in_word")
    private String totalSellPriceInWord;

    @Column(name = "total_price_in_word")
    private String totalPriceInWord;

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "x_file_id")
    private String xFileId;

    @NotNull
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "for_xfile")
    private Boolean forXfile;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private CtsService service;


    public String getNumber() {
        return PrefixEnum.OI + "/" + this.getId();
    }

    public String createDescription() {
        return " Договор № " +
                contract.getName() +
                " от " + contract.getDocDate() + " г." +
                " Общ.сумма " + totalSellPrice +
                " за сервис: " + service.getName() +
                " счет фактура  №" + getNumber() +
                " от " + invoiceDate + " г.";
    }
}
