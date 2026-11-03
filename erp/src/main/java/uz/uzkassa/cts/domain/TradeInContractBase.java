package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class TradeInContractBase extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4213185947L;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "total_product_count", columnDefinition = "integer default 1")
    private Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    private BigDecimal totalPriceWithVat;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    private BigDecimal priceFor1pcWithVat;

    @Column(name = "vat_1pc")
    private BigDecimal vat1pc;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    abstract String getNumber();

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
