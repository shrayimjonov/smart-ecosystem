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
import uz.uzkassa.cts.eventhandler.ServiceOrderInvoiceDateToPersistListener;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cts_service_order_invoice", indexes = {
    @Index(columnList = "contract_id", name = "contract_id_index"),
    @Index(columnList = "x_file_id", name = "cts_service_order_x_file_id_index")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE cts_service_order_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
//to change invoice date on create time
@EntityListeners(ServiceOrderInvoiceDateToPersistListener.class)
public class ServiceOrderInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8905807025L;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate = LocalDateTime.now();

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

    //    @NotNull
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private CtsServiceOrder serviceOrder;

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @OneToMany(mappedBy = "serviceOrderInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderInvoiceDetail> serviceOrderInvoiceDetails = new ArrayList<>();

    @OneToMany(mappedBy = "serviceOrderInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicePaymentHistory> servicePaymentHistories = new ArrayList<>();

    @Column(name = "order_specification_id")
    private Long orderSpecificationId;

    @Column(name = "x_file_id")
    private String xFileId;

    @Column(name = "xfile_status")
    @Enumerated(EnumType.STRING)
    private XfileStatus xfileStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_specification_id", insertable = false, updatable = false)
    private CtsServiceOrderSpecification orderSpecification;

    public void addChild(ServiceOrderInvoiceDetail details) {
        details.setServiceOrderInvoice(this);
        serviceOrderInvoiceDetails.add(details);
    }

    public String getNumber() {
        return PrefixEnum.IS + "/" + this.getId();
    }

    public String createDescriptionForLedger(String tin, Contract contract, String oldInvoiceNumber) {
        return " Договор № " +
            contract.getName() +
            " от " + contract.getDocDate() + " г." +
            " Общ.сумма " + getTotalSellPrice() + "." +
            " Слияние перевод с клиента ИНН:" + tin +
            " Счет фактура: №" + oldInvoiceNumber +
            " счет фактура  №" + getNumber() +
            " от " + getInvoiceDate() + " г.";
    }

    public String createDescription(Contract con) {
        return " Договор № " +
            con.getName() +
            " от " + con.getDocDate() + " г." +
            " Общ.сумма " + getTotalSellPrice() + "." +
            " счет фактура  №" + getNumber() +
            " от " + invoiceDate + " г.";
    }
}
