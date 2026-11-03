package uz.uzkassa.smartpossupply.domain.mark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "mark",
        indexes = {
                @Index(columnList = "code", name = "mark_code_idx"),
                @Index(columnList = "parent_code", name = "mark_parent_code_idx"),
                @Index(columnList = "print_code", name = "mark_print_code_idx"),
                @Index(columnList = "product_id", name = "mark_product_id_idx"),
                @Index(columnList = "company_id", name = "mark_company_id_idx"),
                @Index(columnList = "code, print_code, product_id, company_id", name = "mark_search_keys_index"),
        }
)
@SQLDelete(sql = "UPDATE mark SET deleted = true WHERE id=?")
public class Mark extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 847155L;

    @Column(name = "code")
    String code;

    @Column(name = "print_code")
    String originalCode;

    @Column(name = "parent_code")
    String parentCode;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "mark_order_party_id")
    Long markOrderPartyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_order_party_id", updatable = false, insertable = false)
    MarkOrderParty markOrderParty;

    @Enumerated(EnumType.STRING)
    @Column(name = "turon_status")
    MarkStatus turonStatus;

    @Column(name = "used", columnDefinition = "boolean default false")
    boolean used = false;

    @Column(name = "party_product_id")
    Long partyProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_product_id", updatable = false, insertable = false)
    PartyProduct partyProduct;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.MARK;
    }

    @Override
    public String getEntityName() {
        return getCode();
    }
}
