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

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "one_time_order")
@Entity
@SQLDelete(sql = "UPDATE one_time_order SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class OneTimeOrder extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2977095740L;

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

    @NotNull
    @Column(name = "service_id")
    private Long serviceId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private CtsService service;

    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @Column(name = "file_uri", length = 500)
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OneTimeOrderInvoice invoice;

    @Column(name = "serial_number_kkm")
    private String serialNumberKKM;

    @Column(name = "serial_number_fm")
    private String serialNumberFM;

    public String getNumber() {
        return PrefixEnum.OS + "/" + this.getId();
    }

}
