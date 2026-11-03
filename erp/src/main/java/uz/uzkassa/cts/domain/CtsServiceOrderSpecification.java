package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "cts_service_order_specification")
@Entity
@SQLDelete(sql = "UPDATE cts_service_order_specification SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CtsServiceOrderSpecification extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2766381947L;

    @Column(name = "spec_file_uri", length = 500)
    private String specFileUri;

    @Column(name = "spec_file_name")
    private String specFileName;

    @Column(name = "spec_content_type")
    private String specContentType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @NotNull
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private CtsServiceOrder order;

    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @OneToMany(mappedBy = "serviceOrderSpecification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CtsServiceOrderSpecificationDetail> serviceOrderSpecificationDetails = new ArrayList<>();

    public void addChild(CtsServiceOrderSpecificationDetail details) {
        details.setServiceOrderSpecification(this);
        serviceOrderSpecificationDetails.add(details);
    }

    public String getNumber() {
        return PrefixEnum.SS + "/" + this.getId();
    }

}
