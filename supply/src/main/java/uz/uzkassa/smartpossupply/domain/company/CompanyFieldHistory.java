package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.dto.company.CompanyFieldHistorySuggestionDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.Map;


@Getter
@Setter
@Entity
@Table(name = "company_field_history",
        indexes = {
                @Index(columnList = "company_id, field_type", name = "company_field_history_company_id_field_type_idx")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE company_field_history SET deleted = true WHERE id=?")
public class CompanyFieldHistory extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 771254893L;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "user_id")
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    FieldHistoryType fieldType;

    @NotNull
    @Column(name = "identifier", nullable = false)
    String identifier;

    @Column(name = "display_value")
    String displayValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_data", columnDefinition = "jsonb")
    Map<String, Object> extraData;

    public CompanyFieldHistorySuggestionDTO toSuggestionDTO() {
        return new CompanyFieldHistorySuggestionDTO(getIdentifier(), getDisplayValue(), getExtraData());
    }

    @Override
    public String getName() {
        return displayValue;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY_FIELD_HISTORY;
    }
}
