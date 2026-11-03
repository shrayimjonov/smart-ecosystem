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
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.enums.Action;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "aggregation_history",
    indexes = {
        @Index(columnList = "code", name = "aggregation_history_code_idx"),
        @Index(columnList = "product_id", name = "aggregation_history_product_id_idx"),
        @Index(columnList = "entity_id", name = "aggregation_history_entity_id_idx"),
        @Index(columnList = "entity_type", name = "aggregation_history_entity_type_idx"),
        @Index(columnList = "transfer_type", name = "aggregation_history_transfer_type_idx"),
        @Index(columnList = "code_type", name = "aggregation_history_code_type_idx"),
        @Index(columnList = "action", name = "aggregation_history_action_idx"),
        @Index(
            columnList = "code, product_id, entity_id, entity_type, transfer_type, code_type, action ",
            name = "aggregation_history_search_keys_index"
        ),
    }
)
public class AggregationHistory extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8996514515668L;

    @Column(name = "code")
    String code;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "entity_id")
    Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    EntityTypeEnum entityType;

    @Column(name = "entity_number")
    String entityNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_type")
    CodeType codeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    Action action;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGGREGATION_HISTORY;
    }

    @Override
    public String getEntityName() {
        return code;
    }
}
