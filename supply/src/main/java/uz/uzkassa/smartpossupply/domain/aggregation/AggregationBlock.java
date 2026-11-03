package uz.uzkassa.smartpossupply.domain.aggregation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "aggregation_block", indexes = {
        @Index(columnList = "code", name = "aggregation_block_code_idx"),
        @Index(columnList = "parent_code", name = "aggregation_block_parent_code_idx"),
        @Index(columnList = "print_code", name = "aggregation_block_print_code_idx"),
        @Index(columnList = "product_id", name = "aggregation_block_product_id_idx"),
        @Index(columnList = "company_id", name = "aggregation_block_company_id_idx")
})
@SQLDelete(sql = "UPDATE aggregation_block SET deleted = true WHERE id=?")
public class AggregationBlock extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 94487122L;

    @Column(name = "code")
    String code;

    @Column(name = "print_code")
    String printCode;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty")
    Integer qty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    AggregationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "turon_status")
    MarkStatus turonStatus;

    @Column(name = "aggregation_date")
    LocalDateTime aggregationDate;

    @Column(name = "parent_code")
    String parentCode;

    @Column(name = "used", columnDefinition = "boolean default false")
    boolean used = false;

    @Column(name = "checked", columnDefinition = "boolean default false")
    boolean checked = false;

    @Column(name = "report_id")
    String reportId;

    @Column(name = "mark_order_party_id")
    Long markOrderPartyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_order_party_id", updatable = false, insertable = false)
    MarkOrderParty markOrderParty;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "party_product_id")
    Long partyProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_product_id", updatable = false, insertable = false)
    PartyProduct partyProduct;

    @Override
    public String getName() {
        return getPrintCode();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGGREGATION_BLOCK;
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }
}
